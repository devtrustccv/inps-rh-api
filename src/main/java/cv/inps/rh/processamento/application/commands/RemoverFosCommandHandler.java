package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RemoverFosCommandHandler implements CommandHandler<RemoverFosCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoverFosCommandHandler.class);

  private final FosService fosService;

  public RemoverFosCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(RemoverFosCommand command) {

    LOGGER.debug("RemoverFosCommand : {}", command);

    fosService.removerFos(command.getFosId());

    return ResponseEntity.noContent().build();
  }

}
