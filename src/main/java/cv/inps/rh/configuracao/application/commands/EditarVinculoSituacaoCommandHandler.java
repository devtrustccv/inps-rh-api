package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EditarVinculoSituacaoCommandHandler implements CommandHandler<EditarVinculoSituacaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EditarVinculoSituacaoCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public EditarVinculoSituacaoCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(EditarVinculoSituacaoCommand command) {

    LOGGER.debug("EditarVinculoSituacaoCommand: {}", command);

    paramVinculoService.editarVinculoSituacao(command.getId(), command.getVinculoId(), command.getSituacaoId());

    return ResponseEntity.ok().build();
  }

}
