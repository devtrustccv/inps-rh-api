package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.AlterarSituacaoLaboralCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.dto.AlterarSituacaoLaboralRequest;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlterarSituacaoLaboralWriteService {

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
  public AlterarSituacaoLaboralRequest execute(AlterarSituacaoLaboralCommand command) {

    var dto = command.getAlterarsituacaolaboral();

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
      situacaoLaboral.setObs(dto.getObservacao());
      situacaoLaboralEntityRepository.save(situacaoLaboral);

      funcionario.getValidacoes().stream()
          .filter(v -> v.getEstado() == Estado.P)
          .filter(v -> Referencia.ESTADO_COLABORADOR.name().equals(v.getReferenciaName())
              && TipoAcao.UPDATE.name().equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.setEstado(estado));

      if (paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name())) {
        var dataFimValidacao = DateFormatter.stringToLocalDate(dto.getDataFim());
        tiposRelacionamentoAtual.setDataFim(dataFimValidacao);
        tiposRelacionamentoAtual.setEstActAdm(0);

        var mobilidade = tiposRelacionamentoAtual.getMobId();
        if (mobilidade != null) mobilidade.setDataFim(dataFimValidacao);

        var carreira = tiposRelacionamentoAtual.getCarreiraId();
        if (carreira != null) carreira.setDataFim(dataFimValidacao);

        var contrato = tiposRelacionamentoAtual.getContrVinculoId().getContratoId();
        if (contrato != null) contrato.setDataFim(dataFimValidacao);

        funcionario.getDefinicoesRenumeracoes().forEach(r -> r.setDataFim(dataFimValidacao));
        funcionario.getDefinicoesPagamentos().forEach(p -> p.setDataFim(dataFimValidacao));
      }

      if (estado == Estado.A) {
        ordemServicoWriteService.criar(funcionario, tiposRelacionamentoAtual, dto.getTipoOrdemServico());
      }

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var dataInicio = DateFormatter.stringToLocalDate(dto.getDataInicio());
    var dataFim = DateFormatter.stringToLocalDate(dto.getDataFim());

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    tiposRelacionamentoAtual.setDataFim(dataInicio);
    tiposRelacionamentoAtual.setEstActAdm(0);

    var situacaoLaboral = new SituacaoLaboralEntity();
    situacaoLaboral.setUuid(UuidCreator.getTimeOrdered());
    situacaoLaboral.setSituacaoLaboralId(paramSituacaoLaboral);
    situacaoLaboral.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
    situacaoLaboral.setContrVinculoId(tiposRelacionamentoAtual.getContrVinculoId());
    situacaoLaboral.setObs(dto.getObservacao());
    situacaoLaboral.setDataInicio(dataInicio);
    situacaoLaboral.setDataFim(dataFim);
    situacaoLaboral.setEstado(Estado.P);
    situacaoLaboralEntityRepository.save(situacaoLaboral);

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tiposRelacionamentoAtual);
    tipoRelacionamentoNovo.setDataInicio(dataInicio);
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setObs(dto.getObservacao());
    tipoRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    tipoRelacionamentoNovo.setReferente("SITUACAO_LABORAL");
    var tiprelPersistido = tiposRelacionamentoEntityRepository.saveAndFlush(tipoRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiprelPersistido);
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

    return dto;
  }
}
