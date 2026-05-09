package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class CancelarMissaoServicoCommandHandler implements CommandHandler<CancelarMissaoServicoCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CancelarMissaoServicoCommandHandler.class);

   private final MissaoServicoServiceWrite missaoServicoServiceWrite;

   public CancelarMissaoServicoCommandHandler(MissaoServicoServiceWrite missaoServicoServiceWrite) {
      this.missaoServicoServiceWrite = missaoServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(CancelarMissaoServicoCommand command) {

      LOGGER.debug("CancelarMissaoServicoCommand : {}", command);

      return missaoServicoServiceWrite.cancelar(command);
   }

}
