package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class DeleteVinculoLaboralCommandHandler implements CommandHandler<DeleteVinculoLaboralCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteVinculoLaboralCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public DeleteVinculoLaboralCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(DeleteVinculoLaboralCommand command) {

    paramVinculoService.delete(command.getVinculoLaboralId());

    return ResponseEntity.noContent().build();
  }

}
