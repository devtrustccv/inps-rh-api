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

    // Terceiro caminho da validação (SIM / NAO / CORRIGIR). O fluxo de correção ainda não está
    // implementado: por agora CORRIGIR é um NO-OP — regista no log e devolve 200 com mensagem, SEM
    // validar, actualizar ou mudar qualquer estado. Guard no topo para não tocar em nada.
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      LOGGER.info("[CORRIGIR] ESTADO_COLABORADOR (funcionario={}): opção 'Corrigir' ainda não implementada; nenhuma alteração aplicada.",
          command.getId());
      return new SuccessResponseDTO(false, null, ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO, List.of());
    }

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    var paramSituacaoLaboral = paramSitLaboralEntityRepository.getReferenceById(dto.getSituacaoLaboralId());
    var paramSituacaoLaboralDetalhe = dto.getMotivoId() != null
        ? paramSituacaoDetalheEntityRepository.getReferenceById(dto.getMotivoId()) : null;

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.ESTADO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente para o tipo de acao: UPDATE e referencia: ESTADO_COLABORADOR");
    }

    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
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

      if (paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name())) {
        // Cessação APROVADA → o colaborador fica efetivamente inativo (RH_T_FUNCIONARIOS.ESTADO=I).
        if (estado == Estado.A) funcionario.setEstado(Estado.I);
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

      funcionarioEntityRepository.save(funcionario);
      var mensagem = EstadoValidacao.SIM.equals(dto.getValidar())
          ? "Situação laboral validada."
          : "Situação laboral rejeitada.";
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
    }

    var dataInicio = DateFormatter.stringToLocalDate(dto.getDataInicio());
    var dataFim = DateFormatter.stringToLocalDate(dto.getDataFim());

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(tiposRelacionamentoAtual.getEstado());

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
      // Ainda não processado → UPDATE do registo existente, sem criar novo tipos_relacionamento
      if (situacaoAtual != null) {
        situacaoAtual.setSituacaoLaboralId(paramSituacaoLaboral);
        situacaoAtual.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
        situacaoAtual.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
        situacaoAtual.setDataInicio(dataInicio);
        situacaoAtual.setDataFim(dataFim);
        situacaoAtual.setEstado(Estado.P);
        situacaoLaboralEntityRepository.save(situacaoAtual);
      }
      // FLG_PROCESSA depende de a situação ter remuneração (RH_T_PARAM_SITUACAO.FLG_REMUNERACAO),
      // tal como no ramo processado (novo tiprel). Sem isto, mudar p/ situação sem remuneração
      // (ex.: Licença S/Vencimento) deixava o colaborador ainda marcado para processar salário.
      tiposRelacionamentoAtual.setFlgProcessa(
          Integer.valueOf(1).equals(paramSituacaoLaboral.getFlgRemuneracao()) ? 1 : 0);
      // garante uma validação pendente para esta alteração
      if (funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.ESTADO_COLABORADOR).isEmpty()) {
        var validUpd = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
        validUpd.setFunId(funcionario);
        validUpd.setTiprelId(tiposRelacionamentoAtual);
        validUpd.setReferenciaUuid(situacaoAtual != null ? situacaoAtual.getUuid() : null);
        funcionario.getValidacoes().add(validUpd);
      }
      funcionarioEntityRepository.save(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral actualizada.", List.of());
    }

    // Mudou E já processado → fecha o atual e cria novo registo (situação + tipos_relacionamento)
    // Caso de uso 1.6.3: anterior DATA_FIM = data do registo.
    tiposRelacionamentoAtual.setDataFim(java.time.LocalDate.now());
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
    // Caso de uso 1.6.3: novo tiprel DATA_INICIO = data do registo, DATA_FIM = null.
    tipoRelacionamentoNovo.setDataInicio(java.time.LocalDate.now());
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
