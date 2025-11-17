package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarRenovacaoContratoDTO;

@Component
public class ValidarRenovacaoContratoCommandHandler implements CommandHandler<ValidarRenovacaoContratoCommand, ResponseEntity<ValidarRenovacaoContratoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRenovacaoContratoCommandHandler.class);

   public ValidarRenovacaoContratoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarRenovacaoContratoDTO> handle(ValidarRenovacaoContratoCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}