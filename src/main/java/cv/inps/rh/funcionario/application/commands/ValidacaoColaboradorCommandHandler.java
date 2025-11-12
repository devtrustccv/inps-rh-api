package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;

@Component
public class ValidacaoColaboradorCommandHandler implements CommandHandler<ValidacaoColaboradorCommand, ResponseEntity<AtivarInativarColaboradorDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoColaboradorCommandHandler.class);

   public ValidacaoColaboradorCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<AtivarInativarColaboradorDTO> handle(ValidacaoColaboradorCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}