package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.ValidarContratoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;

@Component
public class ValidarContratoCommandHandler implements CommandHandler<ValidarContratoCommand, ResponseEntity<ValidarContratoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoCommandHandler.class);

   public ValidarContratoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarContratoDTO> handle(ValidarContratoCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}
