package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.ValidarDadosAcademicosDTO;
import cv.inps.rh.funcionario.application.service.ValidarDadosAcademicosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarDadosAcademicosCommandHandler implements CommandHandler<ValidarDadosAcademicosCommand, ResponseEntity<ValidarDadosAcademicosDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosAcademicosCommandHandler.class);

   private final ValidarDadosAcademicosService validarDadosAcademicosService;

   public ValidarDadosAcademicosCommandHandler(ValidarDadosAcademicosService validarDadosAcademicosService) {

     this.validarDadosAcademicosService = validarDadosAcademicosService;
   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarDadosAcademicosDTO> handle(ValidarDadosAcademicosCommand command) {
      LOGGER.info("ValidarDadosAcademicosCommandHandler.handle: {}", command);
      return ResponseEntity.ok(validarDadosAcademicosService.executar(command));
   }

}
