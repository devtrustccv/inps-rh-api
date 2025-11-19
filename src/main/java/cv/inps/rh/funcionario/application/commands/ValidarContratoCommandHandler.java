package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.NovoContratoDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.service.ContratoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ValidarContratoCommandHandler implements CommandHandler<ValidarContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoCommandHandler.class);

  private final ContratoService contratoService;

  public ValidarContratoCommandHandler(ContratoService contratoService) {
    this.contratoService = contratoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(ValidarContratoCommand command) {
    return contratoService.validarContrato(command);
  }

}
