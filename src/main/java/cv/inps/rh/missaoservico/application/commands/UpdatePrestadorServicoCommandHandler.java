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
public class UpdatePrestadorServicoCommandHandler implements CommandHandler<UpdatePrestadorServicoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePrestadorServicoCommandHandler.class);

   private final PrestadorServicoServiceWrite prestadorServicoServiceWrite;

   public UpdatePrestadorServicoCommandHandler(PrestadorServicoServiceWrite prestadorServicoServiceWrite) {
      this.prestadorServicoServiceWrite = prestadorServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(UpdatePrestadorServicoCommand command) {

      LOGGER.debug("UpdatePrestadorServicoCommand : {}", command);

      return prestadorServicoServiceWrite.atualizar(command);
   }

}
