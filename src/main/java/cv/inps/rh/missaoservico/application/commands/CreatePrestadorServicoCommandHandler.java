package cv.inps.rh.missaoservico.application.commands;

import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.PrestadorServicoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class CreatePrestadorServicoCommandHandler implements CommandHandler<CreatePrestadorServicoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreatePrestadorServicoCommandHandler.class);

   private final PrestadorServicoServiceWrite prestadorServicoServiceWrite;

   public CreatePrestadorServicoCommandHandler(PrestadorServicoServiceWrite prestadorServicoServiceWrite) {
      this.prestadorServicoServiceWrite = prestadorServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(CreatePrestadorServicoCommand command) {

      LOGGER.debug("CreatePrestadorServicoCommand : {}", command);

      return prestadorServicoServiceWrite.criar(command);
   }

}
