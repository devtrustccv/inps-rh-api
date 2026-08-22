package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoHistoricoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContratoHistoricoWriteService {

  private final ContratoHistoricoEntityRepository contratoHistoricoEntityRepository;

  /**
   * Regista o historico inicial de um contrato (versao=1, estado=P).
   * Deve ser chamado imediatamente após saveAndFlush do contrato.
   */
  public ContratoHistoricoEntity registrarNovo(ContratoEntity contrato) {
    var h = buildBase(contrato);
    // Versao e POR CONTRATO (DOSSIÊ): o registo inicial de qualquer contrato e sempre 1.
    // Acompanha contrato.getVersao() (=1 para novo contrato), so a renovacao incrementa
    // dentro do mesmo contrato. Multiplos contratos partilharem versao=1/inicial=true e o
    // comportamento esperado — a lista distingue-os por CONTRATO_ID, nao pela versao.
    h.setVersao(contrato.getVersao() != null ? contrato.getVersao() : 1);
    h.setEstado(Estado.P);
    // OBS segue o tipo de situacao do contrato: "INICIO" no 1o contrato (registo colaborador),
    // "NOVO_CONTRATO" num novo contrato. Fallback "NOVO_CONTRATO" se nao vier definido.
    h.setObs(contrato.getTipoSituacao() != null ? contrato.getTipoSituacao() : "NOVO_CONTRATO");
    return contratoHistoricoEntityRepository.save(h);
  }

  /**
   * Regista uma renovação pendente (versao=ultima+1, estado=P) com as novas datas propostas.
   * Deve ser chamado em RenovacaoContratoService antes de saveAndFlush.
   */
  public ContratoHistoricoEntity registrarRenovacaoPendente(ContratoEntity contrato, RenovarContratoReqDTO dto) {
    int nextVersao = contratoHistoricoEntityRepository
        .findTopByContratoId_IdOrderByVersaoDesc(contrato.getId())
        .map(h -> h.getVersao() + 1)
        .orElse(2);

    var h = buildBase(contrato);
    h.setVersao(nextVersao);
    h.setEstado(Estado.P);
    h.setDataInicio(dto.getDataInicio());
    h.setDataFim(dto.getDataFim());
    h.setDuracao(dto.getDuracaoMeses());
    h.setObs("RENOVACAO");
    return contratoHistoricoEntityRepository.save(h);
  }

  /**
   * Ciclo CORRIGIR da RENOVAÇÃO (checker devolve): o histórico pendente da renovação (estado P, versão
   * mais alta) passa a C, sem criar nova versão. O contrato mantém-se A (é o vínculo em vigor); só a
   * proposta de renovação é devolvida para correção. NO-OP se não houver histórico pendente.
   */
  public void marcarRenovacaoPendenteComoCorrecao(ContratoEntity contrato) {
    contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), Estado.P)
        .ifPresent(h -> h.setEstado(Estado.C));
  }

  /**
   * Ciclo CORRIGIR da RENOVAÇÃO (maker reenvia): o histórico da renovação em correção (estado C, versão
   * mais alta) volta a P e recebe as datas/duração corrigidas — sem criar nova versão. Repõe o estado
   * pós-registo para que uma validação SIM posterior corra como uma renovação normal.
   */
  public void reabrirRenovacaoCorrecao(ContratoEntity contrato, RenovarContratoReqDTO dto) {
    contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), Estado.C)
        .ifPresent(h -> {
          h.setEstado(Estado.P);
          if (dto != null) {
            h.setDataInicio(dto.getDataInicio());
            h.setDataFim(dto.getDataFim());
            h.setDuracao(dto.getDuracaoMeses());
          }
        });
  }

  /**
   * Transita contrato + situacaoLaboral pendente + historico pendente num único passo.
   * Deve ser chamado pelos serviços de validação em substituição do padrão
   * setEstado / getSituacoesLaborais / aplicarEstado espalhado.
   */
  public void transicionarEstado(ContratoEntity contrato, Estado estado) {
    // Origem = estado in-flight actual do contrato (P na validação normal, C no reenvio de uma
    // correção do registo de colaborador). Capturado ANTES de mutar; o método adapta-se sozinho
    // ao sentido da transição, sem o caller ter de o declarar.
    var origem = contrato.getEstado();
    contrato.setEstado(estado);
    contrato.getSituacoesLaborais().stream()
        .filter(s -> s.getEstado() == origem)
        .findFirst()
        .ifPresent(s -> s.setEstado(estado));
    aplicarEstado(contrato, origem, estado);
  }

  // origem é passada por transicionarEstado: quando este método corre, contrato.getEstado() já foi
  // mutado para o destino, logo a origem tem de ter sido capturada antes.
  private void aplicarEstado(ContratoEntity contrato, Estado origem, Estado estado) {
    var pendenteOpt = contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), origem);
    if (pendenteOpt.isEmpty()) {
      return;
    }
    var pendente = pendenteOpt.get();

    // Numa validacao positiva, este historico passa a ser o ACTUAL do funcionario:
    // desactiva-se (est_act_adm=0) todos os historicos activos do funcionario
    // (do contrato actual e de contratos anteriores) e activa-se (1) apenas o
    // historico validado. Garante um unico historico activo por funcionario,
    // cobrindo renovacao (novo=1, antigo=0) e mudanca de vinculo/novo contrato.
    if (estado == Estado.A) {
      var funId = contrato.getFunId() != null ? contrato.getFunId().getId() : null;
      if (funId != null) {
        contratoHistoricoEntityRepository
            .findByContratoId_FunId_IdAndEstActAdm(funId, 1)
            .forEach(h -> {
              h.setEstActAdm(0);
              h.setEstado(Estado.I);
              contratoHistoricoEntityRepository.save(h);
            });
      }
      pendente.setEstActAdm(1);
    }

    // O historico do contrato segue as datas do contrato. Propaga as datas atuais do contrato
    // ao historico validado — EXCEPTO renovacao, que traz as suas proprias datas (as novas
    // datas propostas na renovacao).
    if (!"RENOVACAO".equals(pendente.getObs())) {
      pendente.setDataInicio(contrato.getDataInicio());
      pendente.setDataFim(contrato.getDataFim());
      pendente.setDuracao(contrato.getDuracao());
    }

    pendente.setEstado(estado);
    contratoHistoricoEntityRepository.save(pendente);
  }

  private ContratoHistoricoEntity buildBase(ContratoEntity contrato) {
    var h = new ContratoHistoricoEntity();
    h.setContratoId(contrato);
    h.setDataInicio(contrato.getDataInicio());
    h.setDataFim(contrato.getDataFim());
    h.setDuracao(contrato.getDuracao());
    h.setEstActAdm(0);
    h.setUuid(IdentificadorUnico.create().valor());
    return h;
  }
}
