package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SaveSubmissaoServicoCommandHandler implements CommandHandler<SaveSubmissaoServicoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveSubmissaoServicoCommandHandler.class);

   private final MissaoServicoServiceWrite missaoServicoServiceWrite;

   public SaveSubmissaoServicoCommandHandler(MissaoServicoServiceWrite missaoServicoServiceWrite) {
      this.missaoServicoServiceWrite = missaoServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveSubmissaoServicoCommand command) {

      LOGGER.debug("SaveSubmissaoServicoCommand : {}", command);

      return missaoServicoServiceWrite.salvarSubmissao(command);
   }

}
