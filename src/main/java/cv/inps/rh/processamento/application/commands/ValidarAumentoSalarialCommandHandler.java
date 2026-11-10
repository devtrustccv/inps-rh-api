package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidarAumentoSalarialCommandHandler implements CommandHandler<ValidarAumentoSalarialCommand, ResponseEntity<String>> {

  private final AumentoSalarialWriteService aumentoSalarialService;

  public ValidarAumentoSalarialCommandHandler(AumentoSalarialWriteService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarAumentoSalarialCommand command) {

    log.debug("ValidarAumentoSalarialCommand : {}", command);

    aumentoSalarialService.validar(command.getAumentoSalarialId(), command.getEstadoValidacao());

    return ResponseEntity.ok().build();
  }

}
