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
public class JustificarFaltaCommandHandler implements CommandHandler<JustificarFaltaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(JustificarFaltaCommandHandler.class);

   private final JustificarFaltaWriteService justificarFaltaService;

   public JustificarFaltaCommandHandler(JustificarFaltaWriteService justificarFaltaService) {

     this.justificarFaltaService = justificarFaltaService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(JustificarFaltaCommand command) {

      LOGGER.debug("JustificarFaltaCommand : {}", command);


      return ResponseEntity.ok(justificarFaltaService.justificarFalta(command));
   }

}
