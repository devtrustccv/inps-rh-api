package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidarDadosBancariosService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarDadosBancariosCommandHandler implements CommandHandler<ValidarDadosBancariosCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosBancariosCommandHandler.class);

   private final ValidarDadosBancariosService validarDadosBancariosService;
   public ValidarDadosBancariosCommandHandler(ValidarDadosBancariosService validarDadosBancariosService) {

     this.validarDadosBancariosService = validarDadosBancariosService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ValidarDadosBancariosCommand command) {
      LOGGER.info("ValidarDadosBancariosCommandHandler.handle: command={}", command);
      SuccessResponseDTO response = validarDadosBancariosService.executar(command);
      return ResponseEntity.ok(response);
   }

}
