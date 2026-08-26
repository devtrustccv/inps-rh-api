package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.AlterarEstadoContratoCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Ativar/Desativar um contrato e toda a sua cadeia de filhos (tiprel, mobilidade, carreira, regime,
 * situação laboral, subsídios/descontos associados e histórico). É o inverso simétrico da ativação
 * feita na validação SIM ({@link ValidarContratoService}) — mas IMEDIATO (sem ciclo maker/checker) e
 * SEM tocar em {@code funcionario.estado} nem reconciliar movimentos/ordem de serviço: opera apenas
 * ao nível do contrato e dos seus filhos.
 *
 * <p>Guards:
 * <ul>
 *   <li><b>Desativar (A→I):</b> só o contrato ATUAL (tiprel com est_act_adm=1), que esteja ativo e
 *       que NÃO tenha sido processado em folha (RH_T_PROC_FUNCIONARIOS).</li>
 *   <li><b>Ativar (I→A):</b> só o ÚLTIMO contrato do funcionário e desde que não exista outro
 *       contrato em vigor (mesmo guard do registo / Novo Contrato).</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AlterarEstadoContratoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarEstadoContratoService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;

  @Transactional
  public SuccessResponseDTO alterar(AlterarEstadoContratoCommand command) {

    var estadoAlvo = parseEstado(command.getAlterarEstadoContrato().getEstado());

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var contratoUuid = IdentificadorUnico.from(command.getContratoId()).valor();
    var contrato = contratoEntityRepository.findByUuid(contratoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Contrato não encontrado."));

    if (contrato.getFunId() == null || !Objects.equals(contrato.getFunId().getId(), funcionario.getId())) {
      throw IgrpResponseStatusException.badRequest("O contrato indicado não pertence ao funcionário.");
    }

    // Tiprel mais recente do contrato (serve os dois sentidos — ver finder).
    var tiprel = tiposRelacionamentoEntityRepository
        .findFirstByContrVinculoId_UuidOrderByIdDesc(contratoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Não foi encontrada a relação (tiprel) do contrato."));

    if (estadoAlvo == Estado.I) {
      validarDesativacao(contrato, tiprel);
    } else {
      validarAtivacao(funcionario, contrato, contratoUuid);
    }

    aplicarEstado(contrato, tiprel, estadoAlvo);

    funcionarioEntityRepository.saveAndFlush(funcionario);

    var mensagem = estadoAlvo == Estado.I ? "Contrato desativado." : "Contrato ativado.";
    LOGGER.info("[{}] contrato={} funcionario={}", mensagem, contratoUuid, funcionario.getUuid());
    return new SuccessResponseDTO(true, contrato.getUuid().toString(), mensagem, List.of());
  }

  // -----------------------------------------------------------------------------------------------
  // GUARDS
  // -----------------------------------------------------------------------------------------------

  /** Desativar: só o contrato ATUAL (est_act_adm=1), ativo e não processado em folha. */
  private void validarDesativacao(ContratoEntity contrato, TiposRelacionamentoEntity tiprel) {
    if (!Objects.equals(1, tiprel.getEstActAdm())) {
      throw IgrpResponseStatusException.badRequest(
          "Só é possível desativar o contrato atual do funcionário.");
    }
    if (contrato.getEstado() != Estado.A) {
      throw IgrpResponseStatusException.badRequest("Só é possível desativar um contrato ativo.");
    }
    if (processamentoFuncionarioRepository.existsByTiprel_Id(tiprel.getId())) {
      throw IgrpResponseStatusException.badRequest(
          "Não é possível desativar um contrato já processado em folha.");
    }
  }

  /** Ativar: só o ÚLTIMO contrato do funcionário e sem outro contrato em vigor. */
  private void validarAtivacao(FuncionarioEntity funcionario, ContratoEntity contrato, UUID contratoUuid) {
    if (contrato.getEstado() == Estado.A) {
      throw IgrpResponseStatusException.badRequest("O contrato já está ativo.");
    }
    var ultimo = contratoEntityRepository.findTopByFunId_UuidOrderByIdDesc(funcionario.getUuid());
    if (ultimo == null || !Objects.equals(ultimo.getUuid(), contratoUuid)) {
      throw IgrpResponseStatusException.badRequest(
          "Só é possível ativar o último contrato do funcionário.");
    }
    if (contratoEntityRepository.existeContratoEmVigor(funcionario, Estado.A, LocalDate.now())) {
      throw IgrpResponseStatusException.badRequest(
          "O funcionário já possui um contrato ativo em vigor.");
    }
  }

  // -----------------------------------------------------------------------------------------------
  // APLICAÇÃO (flip simétrico)
  // -----------------------------------------------------------------------------------------------

  private void aplicarEstado(ContratoEntity contrato, TiposRelacionamentoEntity tiprel, Estado alvo) {
    // Origem = estado atual do contrato (A quando desativa, I quando ativa). Capturado ANTES de mutar.
    var origem = contrato.getEstado();

    // Contrato + situação laboral (no estado de origem) + histórico. Na ATIVAÇÃO trata também o
    // est_act_adm do histórico (garante um único histórico ativo por funcionário).
    contratoHistoricoWriteService.transicionarEstado(contrato, alvo);

    // Tiprel e o seu est_act_adm.
    tiprel.setEstado(alvo);
    tiprel.setEstActAdm(alvo == Estado.A ? 1 : 0);

    // Filhos diretos do tiprel.
    if (tiprel.getMobId() != null) tiprel.getMobId().setEstado(alvo);
    if (tiprel.getCarreiraId() != null) tiprel.getCarreiraId().setEstado(alvo);
    if (tiprel.getRegimeId() != null) tiprel.getRegimeId().setEstado(alvo);
    if (tiprel.getSituacLaboralId() != null) tiprel.getSituacLaboralId().setEstado(alvo);

    // Subsídios/descontos associados que partilham o estado de origem → passam ao alvo.
    funcionarioRules.getRemuneracoesAssociadosPorEstado(tiprel.getId(), origem)
        .forEach(r -> r.setEstado(alvo));
    funcionarioRules.getPagamentosDescontosAssociadosPorEstado(tiprel.getId(), origem)
        .forEach(p -> p.setEstado(alvo));

    // Histórico: na DESATIVAÇÃO o transicionarEstado não baixa o est_act_adm (só o faz no sentido da
    // ativação). Fecha aqui o(s) histórico(s) ativo(s) deste contrato.
    if (alvo == Estado.I && contrato.getHistoricos() != null) {
      contrato.getHistoricos().stream()
          .filter(h -> Objects.equals(1, h.getEstActAdm()))
          .forEach(h -> h.setEstActAdm(0));
    }
  }

  private Estado parseEstado(String estado) {
    if (estado == null) {
      throw IgrpResponseStatusException.badRequest("Estado é obrigatório (A para ativar, I para desativar).");
    }
    var e = estado.trim().toUpperCase();
    if (e.equals(Estado.A.name())) return Estado.A;
    if (e.equals(Estado.I.name())) return Estado.I;
    throw IgrpResponseStatusException.badRequest("Estado inválido: use 'A' (ativar) ou 'I' (desativar).");
  }

}
