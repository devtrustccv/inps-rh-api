package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.FaltaServiceWrite;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class MarcarFaltaCommandHandler implements CommandHandler<MarcarFaltaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MarcarFaltaCommandHandler.class);

   private final FaltaServiceWrite faltaServiceWrite;

   public MarcarFaltaCommandHandler(FaltaServiceWrite faltaServiceWrite) {

     this.faltaServiceWrite = faltaServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(MarcarFaltaCommand command) {

      LOGGER.debug("MarcarFaltaCommand : {}", command);


     return  ResponseEntity.ok(faltaServiceWrite.marcarFalta(command));
   }

}
