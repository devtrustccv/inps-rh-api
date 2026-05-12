package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.FeriaWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnviarDireitoFeriasCommandHandler implements CommandHandler<EnviarDireitoFeriasCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnviarDireitoFeriasCommandHandler.class);

  private final FeriaWriteService feriaWriteService;

  public EnviarDireitoFeriasCommandHandler(FeriaWriteService feriaWriteService) {
    this.feriaWriteService = feriaWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(EnviarDireitoFeriasCommand command) {
    LOGGER.debug("EnviarDireitoFeriasCommand: {}", command);
    return ResponseEntity.ok(feriaWriteService.enviarDireitoFerias(command));
  }

}
