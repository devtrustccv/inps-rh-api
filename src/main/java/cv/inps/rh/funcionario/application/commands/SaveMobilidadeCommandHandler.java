package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;

@Component
public class SaveMobilidadeCommandHandler implements CommandHandler<SaveMobilidadeCommand, ResponseEntity<MobilidadeDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMobilidadeCommandHandler.class);

   public SaveMobilidadeCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<MobilidadeDTO> handle(SaveMobilidadeCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}