package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EnviarFolhaCommandHandler implements CommandHandler<EnviarFolhaCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnviarFolhaCommandHandler.class);

  private final FosService fosService;

  public EnviarFolhaCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(EnviarFolhaCommand command) {

    LOGGER.debug("EnviarFolhaCommand : {}", command);

    fosService.enviarFolha(command.getFosId());

    return ResponseEntity.ok().build();
  }

}
