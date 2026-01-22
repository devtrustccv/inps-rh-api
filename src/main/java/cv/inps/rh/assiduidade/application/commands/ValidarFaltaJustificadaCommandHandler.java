package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.JustificarFaltaWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarFaltaJustificadaCommandHandler implements CommandHandler<ValidarFaltaJustificadaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarFaltaJustificadaCommandHandler.class);

   private final JustificarFaltaWriteService justificarFaltaService;
   public ValidarFaltaJustificadaCommandHandler(JustificarFaltaWriteService justificarFaltaService) {

     this.justificarFaltaService = justificarFaltaService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarFaltaJustificadaCommand command) {

      LOGGER.debug("ValidarFaltaJustificadaCommand : {}", command);


      return ResponseEntity.ok(justificarFaltaService.validarFaltaJustificada(command));
   }

}
