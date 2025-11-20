package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.NovaMobilidadeService;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamLocalTrabMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.SecaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveMobilidadeCommandHandler implements CommandHandler<SaveMobilidadeCommand, ResponseEntity<MobilidadeDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveMobilidadeCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final MobilidadeMapper mobilidadeMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final SecaoMapper secaoMapper;
  private final ParamLocalTrabMapper paramLocalTrabMapper;

  private final NovaMobilidadeService novaMobilidadeService;

  public SaveMobilidadeCommandHandler(FuncionarioRepository funcionarioRepository,
                                      MobilidadeMapper mobilidadeMapper,
                                      InstituicaoMapper instituicaoMapper,
                                      SecaoMapper secaoMapper,
                                      ParamLocalTrabMapper paramLocalTrabMapper, NovaMobilidadeService novaMobilidadeService) {
    this.funcionarioRepository = funcionarioRepository;
    this.mobilidadeMapper = mobilidadeMapper;
    this.instituicaoMapper = instituicaoMapper;
    this.secaoMapper = secaoMapper;
    this.paramLocalTrabMapper = paramLocalTrabMapper;
    this.novaMobilidadeService = novaMobilidadeService;
  }

  @IgrpCommandHandler
  public ResponseEntity<MobilidadeDTO> handle(SaveMobilidadeCommand command) {

    return ResponseEntity.ok(novaMobilidadeService.save(command));


    /*var dto = command.getMobilidade();
    var funcionarioId = IdentificadorUnico.from(command.getId());

    var funcionario = funcionarioRepository.findById(funcionarioId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId()));

    TiposRelacionamento atual = funcionario.getTipoRelacionamentoAtual();
    if (atual == null) {
      throw IgrpResponseStatusException.badRequest("tiposRelacionamento atual nao encontrado para funcionario");
    }

    var novaDirecao = instituicaoMapper.toDomain(dto.getDirecaoDepois());
    var novaSeccao = secaoMapper.toDomain(dto.getSeccaoDepois());
    var novoLocalTrab = paramLocalTrabMapper.toDomain(dto.getLocalTrabalhoDepois());

    Mobilidade mobilidade;
    if (atual.getMobilidade() != null) {
      mobilidade = atual.getMobilidade();
      mobilidade.update(
          novoLocalTrab,
          dto.getTipoMobilidade() != null ? String.valueOf(dto.getTipoMobilidade()) : mobilidade.getTipoSituacao(),
          novaSeccao,
          novaDirecao,
          null,
          dto.getDataInicio(),
          dto.getDataFim()
      );
    } else {
      mobilidade = Mobilidade.create(
          novoLocalTrab,
          dto.getTipoMobilidade() != null ? String.valueOf(dto.getTipoMobilidade()) : null,
          novaSeccao,
          novaDirecao,
          null,
          dto.getDataInicio(),
          dto.getDataFim()
      );
      funcionario.getMobilidades().add(mobilidade);
    }

    atual.update(
        atual.getCargo(),
        novaDirecao,
        atual.getVinculo(),
        novaSeccao,
        atual.getCategoria(),
        atual.getEscalao(),
        atual.getCarrPcc(),
        atual.getSalario(),
        atual.getMoeda(),
        atual.getRegime(),
        dto.getTipoMobilidade() != null ? String.valueOf(dto.getTipoMobilidade()) : atual.getTipoSituacao(),
        atual.getTiprelAnterior(),
        atual.getFlgProcessa(),
        atual.getObs(),
        dto.getDataInicio(),
        dto.getDataFim(),
        atual.getContrato().getDataInicio(),
        atual.getContrato().getDataFim(),
        atual.getContrato(),
        atual.getCarreira(),
        mobilidade,
        novoLocalTrab,
        atual.getRegimeTrabalho(),
        atual.getTipoContrato(),
        atual.getReferente(),
        atual.getUltProc(),
        atual.getMotivoSitLab(),
        atual.getSituacLaboral(),
        atual.getTpContrato()
    );

    if (dto.getValidar() != null) {
      Estado estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mobilidade.mudarEstado(estado);
      atual.mudarEstado(estado);
    }

    funcionarioRepository.save(funcionario);

    return ResponseEntity.ok(mobilidadeMapper.mobilidadeDTO(mobilidade));*/
  }

}
