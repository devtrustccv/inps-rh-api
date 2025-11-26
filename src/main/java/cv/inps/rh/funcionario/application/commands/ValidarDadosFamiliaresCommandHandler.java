package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidarAgregadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;

@Component
public class ValidarDadosFamiliaresCommandHandler implements CommandHandler<ValidarDadosFamiliaresCommand, ResponseEntity<ValidarAgregadosDependentesDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosFamiliaresCommandHandler.class);

   private final ValidarAgregadosService validarAgregadosService;
   public ValidarDadosFamiliaresCommandHandler(ValidarAgregadosService validarAgregadosService) {

     this.validarAgregadosService = validarAgregadosService;
   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarAgregadosDependentesDTO> handle(ValidarDadosFamiliaresCommand command) {
      LOGGER.info("Handling validar dados familiares command with request: {}", command);
      return ResponseEntity.ok(validarAgregadosService.executar(command));
   }

}
