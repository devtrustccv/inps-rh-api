package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarProcessamentoSalarialCommandHandler implements CommandHandler<ValidarProcessamentoSalarialCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarProcessamentoSalarialCommandHandler.class);

  private final ProcessamentoSalarialWriteService processamentoSalarialService;

  public ValidarProcessamentoSalarialCommandHandler(ProcessamentoSalarialWriteService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarProcessamentoSalarialCommand command) {

    LOGGER.debug("ValidarProcessamentoSalarialCommand : {}", command);

    processamentoSalarialService.validarProcessamentoSalarial(command.getValidarprocessamentorequest().idsProcessamento());

    return ResponseEntity.ok().build();
  }

}
