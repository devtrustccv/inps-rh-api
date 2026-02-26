package cv.inps.rh.transversal.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.transversal.application.dto.DossierResponseDTO;

@Component
public class ObterDossierColaboradorCommandHandler implements CommandHandler<ObterDossierColaboradorCommand, ResponseEntity<DossierResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ObterDossierColaboradorCommandHandler.class);

   public ObterDossierColaboradorCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<DossierResponseDTO> handle(ObterDossierColaboradorCommand command) {

      LOGGER.debug("ObterDossierColaboradorCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}