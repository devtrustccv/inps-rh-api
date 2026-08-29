package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.AlterarSituacaoLaboralCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.AusenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AusenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SituacaoLaboralEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlterarSituacaoLaboralWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarSituacaoLaboralWriteService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final AusenciaEntityRepository ausenciaEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;

  @Transactional
  public SuccessResponseDTO execute(AlterarSituacaoLaboralCommand command) {

    var dto = command.getAlterarsituacaolaboral();

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    // CORRIGIR (checker devolve ao maker): situação pendente P -> C e validação P -> C, SEM aplicar
    // payload. O maker corrige e reenvia por este mesmo endpoint com validar=null (C -> P). Âncora =
    // situacaoLaboral.uuid (referencia_uuid da validação UPDATE/ESTADO_COLABORADOR).
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      var tiprelParaCorrigir = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      var sitParaCorrigir = tiprelParaCorrigir.getSituacLaboralId();
      if (sitParaCorrigir == null || sitParaCorrigir.getEstado() != Estado.P
          || !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.ESTADO_COLABORADOR)) {
        throw IgrpResponseStatusException.badRequest(
            "Não há alteração de situação laboral pendente para devolver para correção.");
      }
      funcionarioRules.devolverParaCorrecao(sitParaCorrigir.getUuid(), Estado.P, Referencia.ESTADO_COLABORADOR);
      sitParaCorrigir.setEstado(Estado.C);
      tiprelParaCorrigir.setEstado(Estado.C);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      LOGGER.info("[CORRIGIR] ESTADO_COLABORADOR devolvido para correção (situacao={}).", sitParaCorrigir.getUuid());
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Situação laboral devolvida para correção.", List.of());
    }

    var paramSituacaoLaboral = paramSitLaboralEntityRepository.getReferenceById(dto.getSituacaoLaboralId());
    var paramSituacaoLaboralDetalhe = dto.getMotivoId() != null
        ? paramSituacaoDetalheEntityRepository.getReferenceById(dto.getMotivoId()) : null;

    // Guard: um registo em correção (C) não pode ser validado antes de reenviado pelo maker.
    boolean estaPorCorrigir = funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.ESTADO_COLABORADOR);
    if (estaPorCorrigir && dto.getValidar() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Situação laboral em correção: não pode ser validada. Corrija e reenvie primeiro.");
    }

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.ESTADO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente para o tipo de acao: UPDATE e referencia: ESTADO_COLABORADOR");
    }

    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      // Discriminador do ramo de registo: só o ramo "processado" cria um NOVO tiprel pendente (estado=P)
      // que fechou o anterior. No ramo "não processado" (UPDATE in place) o tiprel fica estado=A e nenhum
      // predecessor foi fechado por esta alteração — logo o rollback de rejeição NÃO deve reabrir nada.
      boolean tiprelEraPendente = tiposRelacionamentoAtual.getEstado() == Estado.P;
      tiposRelacionamentoAtual.setEstado(estado);

      var situacaoLaboral = tiposRelacionamentoAtual.getSituacLaboralId();
      situacaoLaboral.setEstado(estado);
      situacaoLaboral.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
      situacaoLaboral.setSituacaoLaboralId(paramSituacaoLaboral);
      situacaoLaboral.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
      situacaoLaboralEntityRepository.save(situacaoLaboral);

      funcionario.getValidacoes().stream()
          .filter(v -> v.getEstado() == Estado.P)
          .filter(v -> Referencia.ESTADO_COLABORADOR.name().equals(v.getReferenciaName())
              && TipoAcao.UPDATE.name().equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.setEstado(estado));

      if (estado == Estado.A && paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name())) {
        // Cessação APROVADA → o colaborador fica efetivamente inativo (RH_T_FUNCIONARIOS.ESTADO=I).
        // Os efeitos de fecho (datas fim em mobilidade/carreira/contrato/rem/pag) só se aplicam na
        // aprovação; numa rejeição são revertidos pelo rollback abaixo.
        funcionario.setEstado(Estado.I);
        var dataFimValidacao = DateFormatter.stringToLocalDate(dto.getDataFim());
        tiposRelacionamentoAtual.setDataFim(dataFimValidacao);
        tiposRelacionamentoAtual.setEstActAdm(0);

        var mobilidade = tiposRelacionamentoAtual.getMobId();
        if (mobilidade != null) mobilidade.setDataFim(dataFimValidacao);

        var carreira = tiposRelacionamentoAtual.getCarreiraId();
        if (carreira != null) carreira.setDataFim(dataFimValidacao);

        var contrato = tiposRelacionamentoAtual.getContrVinculoId();
        if (contrato != null) contrato.setDataFim(dataFimValidacao);

        funcionario.getDefinicoesRenumeracoes().forEach(r -> r.setDataFim(dataFimValidacao));
        funcionario.getDefinicoesPagamentos().forEach(p -> p.setDataFim(dataFimValidacao));
      }

      if (estado == Estado.A) {
        ordemServicoWriteService.criar(funcionario, tiposRelacionamentoAtual, dto.getTipoOrdemServico());
      }

      if (estado == Estado.I) {
        // Rejeição (checker devolve NAO): rollback ao estado pré-registo. O registo (maker) fechou o
        // tiprel anterior (est_act_adm=0) e criou este novo pendente; ao rejeitar, reabrir o anterior e
        // descartar o novo, repondo RH_T_FUNCIONARIOS.ESTADO conforme a situação que estava vigente.
        var tiprelAnterior = tiposRelacionamentoAtual.getTiprelId();
        boolean anteriorFechadoPeloRegisto = tiprelEraPendente && tiprelAnterior != null
            && Integer.valueOf(0).equals(tiprelAnterior.getEstActAdm());
        if (anteriorFechadoPeloRegisto) {
          tiprelAnterior.setEstActAdm(1);
          tiprelAnterior.setDataFim(null);
          tiposRelacionamentoEntityRepository.save(tiprelAnterior);
          tiposRelacionamentoAtual.setEstActAdm(0);

          var situacaoAnterior = tiprelAnterior.getSituacLaboralId();
          var codigoAnterior = (situacaoAnterior != null && situacaoAnterior.getSituacaoLaboralId() != null)
              ? situacaoAnterior.getSituacaoLaboralId().getCodigo() : null;
          funcionario.setEstado(SituacaoLaboral.CESSADO.name().equals(codigoAnterior) ? Estado.I : Estado.A);
        }
      }

      funcionarioEntityRepository.save(funcionario);
      var mensagem = EstadoValidacao.SIM.equals(dto.getValidar())
          ? "Situação laboral validada."
          : "Situação laboral rejeitada.";
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
    }

    // Spec DOSSIÊ (Inativar/Ativar): "o utilizador deve indicar obrigatoriamente o motivo da alteração".
    // Guard aplicado ao caminho de registo/reenvio (maker); a validação (checker) já reaproveita o motivo.
    if (dto.getMotivoId() == null) {
      throw IgrpResponseStatusException.badRequest(
          "O motivo da alteração da situação laboral é obrigatório.");
    }

    // Guard do combo Inativar/Ativar (2 opções): não faz sentido CESSAR quem já está inativo, nem ATIVAR
    // quem já está ativo. Aplica-se só às situações ATIVO/CESSADO; outras (ex.: Licença S/Vencimento)
    // passam sem esta restrição. Não se aplica ao reenvio de correção (o maker reenvia o mesmo estado).
    if (!estaPorCorrigir) {
      var codigoAlvo = paramSituacaoLaboral.getCodigo();
      if (SituacaoLaboral.CESSADO.name().equals(codigoAlvo) && funcionario.getEstado() == Estado.I) {
        throw IgrpResponseStatusException.badRequest("O colaborador já está inativo; não é necessária esta ação.");
      }
      if (SituacaoLaboral.ATIVO.name().equals(codigoAlvo) && funcionario.getEstado() == Estado.A) {
        throw IgrpResponseStatusException.badRequest("O colaborador já está ativo; não é necessária esta ação.");
      }
    }

    var dataInicio = DateFormatter.stringToLocalDate(dto.getDataInicio());
    var dataFim = DateFormatter.stringToLocalDate(dto.getDataFim());

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(tiposRelacionamentoAtual.getEstado());

    // Maker reenvia a correção (C -> P): a situação atual está em correção. Aplica as edições in place
    // no registo devolvido e reabre para validação — SEM criar novo tipos_relacionamento (a correção é
    // sobre o mesmo registo que o checker devolveu).
    if (estaPorCorrigir) {
      var sit = tiposRelacionamentoAtual.getSituacLaboralId();
      if (sit == null) {
        throw IgrpResponseStatusException.badRequest("Situação laboral em correção não encontrada.");
      }
      sit.setSituacaoLaboralId(paramSituacaoLaboral);
      sit.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
      sit.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
      sit.setDataInicio(dataInicio);
      sit.setDataFim(dataFim);
      sit.setEstado(Estado.P);
      tiposRelacionamentoAtual.setEstado(Estado.P);
      tiposRelacionamentoAtual.setFlgProcessa(
          Integer.valueOf(1).equals(paramSituacaoLaboral.getFlgRemuneracao()) ? 1 : 0);
      var validacaoReaberta = funcionarioRules.reabrirParaValidacao(sit.getUuid(), Referencia.ESTADO_COLABORADOR);
      // Auto-audit (JaVers): carimba o save da correção com a validação em curso — o detalhe de
      // alterações filtra por este validacaoUuid. O baseline já existe do registo da situação.
      try {
        ValidacaoAuditContext.set(validacaoReaberta.getId(), validacaoReaberta.getUuid(), "RH_T_SITUACAO_LABORAL");
        situacaoLaboralEntityRepository.save(sit);
      } finally {
        ValidacaoAuditContext.clear();
      }
      funcionarioEntityRepository.saveAndFlush(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Situação laboral corrigida e reenviada para validação.", List.of());
    }

    // Caso de teste (Situação Laboral): só há registo novo quando muda Situação/Motivo E o registo
    // atual já foi processado. Se mudou mas ainda não processado → apenas UPDATE. Sem alteração → nada.
    var situacaoAtual = tiposRelacionamentoAtual.getSituacLaboralId();
    Long sitAtualId = (situacaoAtual != null && situacaoAtual.getSituacaoLaboralId() != null)
        ? situacaoAtual.getSituacaoLaboralId().getId() : null;
    Long motAtualId = (situacaoAtual != null && situacaoAtual.getMotivoSitLabId() != null)
        ? situacaoAtual.getMotivoSitLabId().getId() : null;
    boolean mudouSituacaoOuMotivo = !Objects.equals(sitAtualId, dto.getSituacaoLaboralId())
        || !Objects.equals(motAtualId, dto.getMotivoId());

    if (!mudouSituacaoOuMotivo) {
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral sem alterações.", List.of());
    }

    boolean processado = tiposRelacionamentoAtual.getUltProc() != null;
    if (!processado) {
      // Ainda não processado → UPDATE do registo existente, sem criar novo tipos_relacionamento.
      // FLG_PROCESSA depende de a situação ter remuneração (RH_T_PARAM_SITUACAO.FLG_REMUNERACAO),
      // tal como no ramo processado (novo tiprel). Sem isto, mudar p/ situação sem remuneração
      // (ex.: Licença S/Vencimento) deixava o colaborador ainda marcado para processar salário.
      tiposRelacionamentoAtual.setFlgProcessa(
          Integer.valueOf(1).equals(paramSituacaoLaboral.getFlgRemuneracao()) ? 1 : 0);

      // Garante uma validação pendente para esta alteração e persiste-a ANTES do save da situação,
      // para carimbar o auto-audit (JaVers) — sem isto o "detalhe de alterações" do registo fica vazio.
      var validacaoPend = funcionarioRules
          .getValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.ESTADO_COLABORADOR)
          .orElse(null);
      if (validacaoPend == null) {
        var validUpd = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
        validUpd.setFunId(funcionario);
        validUpd.setTiprelId(tiposRelacionamentoAtual);
        validUpd.setReferenciaUuid(situacaoAtual != null ? situacaoAtual.getUuid() : null);
        funcionario.getValidacoes().add(validUpd);
        funcionarioEntityRepository.saveAndFlush(funcionario); // persiste a validação -> id/uuid
        validacaoPend = validUpd;
      }

      if (situacaoAtual != null) {
        situacaoAtual.setSituacaoLaboralId(paramSituacaoLaboral);
        situacaoAtual.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
        situacaoAtual.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
        situacaoAtual.setDataInicio(dataInicio);
        situacaoAtual.setDataFim(dataFim);
        situacaoAtual.setEstado(Estado.P);
        // Auto-audit (JaVers): carimba o UPDATE da situação com a validação em curso — o detalhe de
        // alterações filtra por este validacaoUuid (situacaoLaboralId/motivo/datas/obs).
        try {
          ValidacaoAuditContext.set(validacaoPend.getId(), validacaoPend.getUuid(), "RH_T_SITUACAO_LABORAL");
          situacaoLaboralEntityRepository.save(situacaoAtual);
        } finally {
          ValidacaoAuditContext.clear();
        }
      }
      funcionarioEntityRepository.save(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral actualizada.", List.of());
    }

    // Mudou E já processado → fecha o atual e cria novo registo (situação + tipos_relacionamento)
    // Spec DOSSIÊ 1.1: anterior DATA_FIM = data início (do formulário).
    tiposRelacionamentoAtual.setDataFim(dataInicio);
    tiposRelacionamentoAtual.setEstActAdm(0);

    var situacaoLaboral = new SituacaoLaboralEntity();
    situacaoLaboral.setUuid(UuidCreator.getTimeOrdered());
    situacaoLaboral.setSituacaoLaboralId(paramSituacaoLaboral);
    situacaoLaboral.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
    situacaoLaboral.setContrVinculoId(tiposRelacionamentoAtual.getContrVinculoId());
    situacaoLaboral.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    situacaoLaboral.setDataInicio(dataInicio);
    situacaoLaboral.setDataFim(dataFim);
    situacaoLaboral.setEstado(Estado.P);
    situacaoLaboralEntityRepository.save(situacaoLaboral);

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tiposRelacionamentoAtual);
    // Spec DOSSIÊ 1.2: novo tiprel DATA_INICIO = data início (do formulário), DATA_FIM = null.
    tipoRelacionamentoNovo.setDataInicio(dataInicio);
    tipoRelacionamentoNovo.setDataFim(null);
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    tipoRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    tipoRelacionamentoNovo.setReferente("SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setEstado(Estado.P);
    // FLG_PROCESSA depende de a situação ter remuneração (RH_T_PARAM_SITUACAO.FLG_REMUNERACAO)
    tipoRelacionamentoNovo.setFlgProcessa(Integer.valueOf(1).equals(paramSituacaoLaboral.getFlgRemuneracao()) ? 1 : 0);
    var tiprelPersistido = tiposRelacionamentoEntityRepository.saveAndFlush(tipoRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiprelPersistido);
    valid.setReferenciaUuid(situacaoLaboral.getUuid());
    funcionario.getValidacoes().add(valid);

    funcionario.setEstado(paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name()) ? Estado.I :
        paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.ATIVO.name()) ? Estado.A :
            funcionario.getEstado());

    funcionarioEntityRepository.save(funcionario);

    var entradasAntigas = tipoRelRemPagEntityRepository.findByTiprelId_Id(tiposRelacionamentoAtual.getId());
    var novasEntradas = entradasAntigas.stream().map(e -> {
      var nova = new TipoRelRemPagEntity();
      nova.setTiprelId(tiprelPersistido);
      nova.setRemId(e.getRemId());
      nova.setPagId(e.getPagId());
      return nova;
    }).toList();
    tipoRelRemPagEntityRepository.saveAll(novasEntradas);

    if (Integer.valueOf(1).equals(paramSituacaoLaboral.getFlgAusencia())) {
      var ausencia = new AusenciaEntity();
      ausencia.setUuid(UuidCreator.getTimeOrdered());
      ausencia.setParamSitId(paramSituacaoLaboral);
      ausencia.setDataInicio(dataInicio);
      ausencia.setDataFim(dataFim);
      ausencia.setReferenciaName("SITUACAO_LABORAL");
      ausencia.setReferenciaId(situacaoLaboral.getId());
      ausencia.setEstado(Estado.P);
      ausenciaEntityRepository.save(ausencia);
    }

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral actualizada.", List.of());
  }
}
