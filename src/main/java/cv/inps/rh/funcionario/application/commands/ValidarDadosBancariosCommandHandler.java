package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;

@Component
public class ValidarDadosBancariosCommandHandler implements CommandHandler<ValidarDadosBancariosCommand, ResponseEntity<ValidarDadosBancariosDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosBancariosCommandHandler.class);

   public ValidarDadosBancariosCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarDadosBancariosDTO> handle(ValidarDadosBancariosCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}