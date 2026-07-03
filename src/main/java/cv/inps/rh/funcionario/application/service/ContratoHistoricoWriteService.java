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
    h.setVersao(1);
    h.setEstado(Estado.P);
    h.setObs("NOVO_CONTRATO");
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
   * Transita contrato + situacaoLaboral pendente + historico pendente num único passo.
   * Deve ser chamado pelos serviços de validação em substituição do padrão
   * setEstado / getSituacoesLaborais / aplicarEstado espalhado.
   */
  public void transicionarEstado(ContratoEntity contrato, Estado estado) {
    contrato.setEstado(estado);
    contrato.getSituacoesLaborais().stream()
        .filter(s -> s.getEstado() == Estado.P)
        .findFirst()
        .ifPresent(s -> s.setEstado(estado));
    aplicarEstado(contrato, estado);
  }

  public void aplicarEstado(ContratoEntity contrato, Estado estado) {
    var pendenteOpt = contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), Estado.P);
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
