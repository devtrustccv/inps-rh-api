package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarDadosAcademicosDTO;

@Component
public class ValidarDadosAcademicosCommandHandler implements CommandHandler<ValidarDadosAcademicosCommand, ResponseEntity<ValidarDadosAcademicosDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosAcademicosCommandHandler.class);

   public ValidarDadosAcademicosCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarDadosAcademicosDTO> handle(ValidarDadosAcademicosCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}