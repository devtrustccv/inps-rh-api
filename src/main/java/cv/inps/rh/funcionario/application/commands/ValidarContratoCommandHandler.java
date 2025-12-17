package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.service.ValidarContratoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarContratoCommandHandler implements CommandHandler<ValidarContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoCommandHandler.class);

  private final ValidarContratoService contratoService;

  public ValidarContratoCommandHandler(ValidarContratoService contratoService) {
    this.contratoService = contratoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(ValidarContratoCommand command) {
    return contratoService.validar(command);
  }

}
