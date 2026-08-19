package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidarContratoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarContratoCommandHandler implements CommandHandler<ValidarContratoCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoCommandHandler.class);

  private final ValidarContratoService contratoService;

  public ValidarContratoCommandHandler(ValidarContratoService contratoService) {
    this.contratoService = contratoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(ValidarContratoCommand command) {
    return contratoService.validar(command);
  }

}
