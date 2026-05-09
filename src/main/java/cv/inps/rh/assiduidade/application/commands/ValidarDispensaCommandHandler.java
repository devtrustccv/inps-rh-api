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
public class ValidarDispensaCommandHandler implements CommandHandler<ValidarDispensaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDispensaCommandHandler.class);

  private final DispensaWriteService dispensaWriteService;

   public ValidarDispensaCommandHandler(DispensaWriteService dispensaWriteService) {

     this.dispensaWriteService = dispensaWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarDispensaCommand command) {

      LOGGER.debug("ValidarDispensaCommand : {}", command);


      return ResponseEntity.ok(dispensaWriteService.validarDispensa(command));
   }

}
