package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EliminarCarreiraCommandHandler implements CommandHandler<EliminarCarreiraCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EliminarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public EliminarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(EliminarCarreiraCommand command) {

    LOGGER.debug("EliminarCarreiraCommand : {}", command);

    carreiraWriteService.eliminarCareira(command.getCarreiraId());

    return ResponseEntity.noContent().build();
  }

}
