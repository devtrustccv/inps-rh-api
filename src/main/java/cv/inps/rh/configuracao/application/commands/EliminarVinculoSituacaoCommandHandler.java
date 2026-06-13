package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EliminarVinculoSituacaoCommandHandler implements CommandHandler<EliminarVinculoSituacaoCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EliminarVinculoSituacaoCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public EliminarVinculoSituacaoCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(EliminarVinculoSituacaoCommand command) {

    LOGGER.debug("EliminarVinculoSituacaoCommand: {}", command);

    paramVinculoService.eliminarVinculoSituacao(command.getId());

    return ResponseEntity.noContent().build();
  }

}
