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
public class SaveMissaoServicoAutorizacaoCommandHandler implements CommandHandler<SaveMissaoServicoAutorizacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoServicoAutorizacaoCommandHandler.class);

   private final MissaoServicoServiceWrite missaoServicoServiceWrite;

   public SaveMissaoServicoAutorizacaoCommandHandler(MissaoServicoServiceWrite missaoServicoServiceWrite) {
      this.missaoServicoServiceWrite = missaoServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveMissaoServicoAutorizacaoCommand command) {

      LOGGER.debug("SaveMissaoServicoAutorizacaoCommand : {}", command);

      return missaoServicoServiceWrite.salvarAutorizacao(command);
   }

}
