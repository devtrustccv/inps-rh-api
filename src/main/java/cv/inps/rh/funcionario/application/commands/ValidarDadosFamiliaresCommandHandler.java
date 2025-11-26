package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;

@Component
public class ValidarDadosFamiliaresCommandHandler implements CommandHandler<ValidarDadosFamiliaresCommand, ResponseEntity<ValidarAgregadosDependentesDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosFamiliaresCommandHandler.class);

   public ValidarDadosFamiliaresCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarAgregadosDependentesDTO> handle(ValidarDadosFamiliaresCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}