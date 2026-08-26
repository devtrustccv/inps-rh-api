package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class FinalizarSoatCommandHandler implements CommandHandler<FinalizarSoatCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FinalizarSoatCommandHandler.class);

  private final SoatService soatService;

  public FinalizarSoatCommandHandler(SoatService soatService) {
    this.soatService = soatService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(FinalizarSoatCommand command) {

    LOGGER.debug("FinalizarSoatCommand : {}", command);

    soatService.finalizarSoat(command.getSoatId());

    return ResponseEntity.ok().build();
  }

}
