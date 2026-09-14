package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.PrestadorServicoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdatePrestadorServicoCommandHandler implements CommandHandler<UpdatePrestadorServicoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePrestadorServicoCommandHandler.class);

   private final PrestadorServicoServiceWrite prestadorServicoServiceWrite;

   public UpdatePrestadorServicoCommandHandler(PrestadorServicoServiceWrite prestadorServicoServiceWrite) {
      this.prestadorServicoServiceWrite = prestadorServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdatePrestadorServicoCommand command) {

      LOGGER.debug("UpdatePrestadorServicoCommand : {}", command);

      return prestadorServicoServiceWrite.atualizar(command);
   }

}
