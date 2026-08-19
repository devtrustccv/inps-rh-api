package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EliminarCarreiraCommandHandler implements CommandHandler<EliminarCarreiraCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EliminarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public EliminarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(EliminarCarreiraCommand command) {

    LOGGER.debug("EliminarCarreiraCommand : {}", command);

    return ResponseEntity.ok(carreiraWriteService.eliminarCareira(command.getCarreiraId()));
  }

}
