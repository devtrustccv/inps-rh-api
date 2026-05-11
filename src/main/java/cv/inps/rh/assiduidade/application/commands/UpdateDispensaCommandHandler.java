package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.DispensaWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateDispensaCommandHandler implements CommandHandler<UpdateDispensaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDispensaCommandHandler.class);

   private final DispensaWriteService dispensaWriteService;

   public UpdateDispensaCommandHandler(DispensaWriteService dispensaWriteService) {

     this.dispensaWriteService = dispensaWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateDispensaCommand command) {

      LOGGER.debug("UpdateDispensaCommand : {}", command);


      return ResponseEntity.ok(dispensaWriteService.updateDispensa(command));
   }

}
