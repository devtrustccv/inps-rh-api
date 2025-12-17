package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.FeriadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveFeriadosCommandHandler implements CommandHandler<SaveFeriadosCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveFeriadosCommandHandler.class);

  private final FeriadoService feriadoService;

  public SaveFeriadosCommandHandler(FeriadoService feriadoService) {
    this.feriadoService = feriadoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveFeriadosCommand command) {

    LOGGER.debug("SaveFeriadosCommand : {}", command);

    feriadoService.save(command.getFeriadolistrequest());

    return ResponseEntity.ok().build();
  }

}
