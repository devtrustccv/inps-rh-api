package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.InativarAtivarColaboradorCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SituacaoLaboralColaboradorWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;

  @Transactional
  public AtivarInativarColaboradorDTO execute(InativarAtivarColaboradorCommand command) {

    var dto = command.getAtivarinativarcolaborador();

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    var paramSituacaoLaboral = paramSitLaboralEntityRepository.getReferenceById(dto.getSituacaoLaboralId());
    var paramSituacaoLaboralDetalhe = paramSituacaoDetalheEntityRepository.getReferenceById(dto.getMotivoId());

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

      if(paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name())){
        tiposRelacionamentoAtual.setDataFim(LocalDate.now());
        tiposRelacionamentoAtual.setEstActAdm(0);

        var mobilidade = tiposRelacionamentoAtual.getMobId();
        if(mobilidade != null) mobilidade.setDataFim(LocalDate.now());

        var carreira = tiposRelacionamentoAtual.getCarreiraId();
        if(carreira != null) carreira.setDataFim(LocalDate.now());

        var contrato = tiposRelacionamentoAtual.getContrVinculoId().getContratoId();
        if(contrato != null) contrato.setDataFim(LocalDate.now());

        funcionario.getDefinicoesRenumeracoes()
            .forEach(r -> r.setDataFim(LocalDate.now()));

        funcionario.getDefinicoesPagamentos().forEach(p -> p.setDataFim(LocalDate.now()));
      }

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }



    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    tiposRelacionamentoAtual.setDataFim(LocalDate.now());
    tiposRelacionamentoAtual.setEstActAdm(0);

    var situacaoLaboral = new SituacaoLaboralEntity();
    situacaoLaboral.setUuid(UuidCreator.getTimeOrdered());
    situacaoLaboral.setSituacaoLaboralId(paramSituacaoLaboral);
    situacaoLaboral.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
    situacaoLaboral.setContrVinculoId(tiposRelacionamentoAtual.getContrVinculoId());
    situacaoLaboral.setObs(dto.getObservacao());
    situacaoLaboral.setDataInicio(LocalDate.now());
    situacaoLaboral.setEstado(Estado.P);
    situacaoLaboralEntityRepository.save(situacaoLaboral);

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tiposRelacionamentoAtual);
    tipoRelacionamentoNovo.setDataInicio(LocalDate.now());
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setObs(dto.getObservacao());
    tipoRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    tipoRelacionamentoNovo.setReferente("SITUACAO_LABORAL");
    funcionario.getTiposrelacionamentos().add(tipoRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tipoRelacionamentoNovo);
    funcionario.getValidacoes().add(valid);

    funcionario.setEstado(paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name()) ? Estado.I :
        paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.ATIVO.name()) ? Estado.A :
            funcionario.getEstado());

    funcionarioEntityRepository.save(funcionario);
    return dto;
  }
}
