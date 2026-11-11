package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ValidarSubsidiosFeriasCommandHandler implements CommandHandler<ValidarSubsidiosFeriasCommand, ResponseEntity<Map<String, Object>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarSubsidiosFeriasCommandHandler.class);

  private final SubsidioService subsidioService;

  public ValidarSubsidiosFeriasCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, Object>> handle(ValidarSubsidiosFeriasCommand command) {

    LOGGER.debug("ValidarSubsidiosFeriasCommand : {}", command);

    var total = subsidioService.validarSubsidiosFerias(command.getRequest().getSubsidioIds());
    return ResponseEntity.ok(Map.of("validados", total));
  }

}
