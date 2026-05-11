package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.HoraExtraServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class MarcarHoraExtraCommandHandler implements CommandHandler<MarcarHoraExtraCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MarcarHoraExtraCommandHandler.class);

   private final HoraExtraServiceWrite horaExtraServiceWrite;

   public MarcarHoraExtraCommandHandler(HoraExtraServiceWrite horaExtraServiceWrite) {

     this.horaExtraServiceWrite = horaExtraServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(MarcarHoraExtraCommand command) {

      LOGGER.debug("MarcarHoraExtraCommand : {}", command);

      return ResponseEntity.ok(horaExtraServiceWrite.marcarHoraExtra(command));
   }

}
