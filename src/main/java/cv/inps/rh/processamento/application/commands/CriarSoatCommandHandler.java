package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class CriarSoatCommandHandler implements CommandHandler<CriarSoatCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CriarSoatCommandHandler.class);

  private final SoatService soatService;

  public CriarSoatCommandHandler(SoatService soatService) {
    this.soatService = soatService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(CriarSoatCommand command) {

    LOGGER.debug("CriarSoatCommand : {}", command);

    soatService.criarSoat(command.getAno(), command.getMes());

    return ResponseEntity.ok().build();
  }

}
