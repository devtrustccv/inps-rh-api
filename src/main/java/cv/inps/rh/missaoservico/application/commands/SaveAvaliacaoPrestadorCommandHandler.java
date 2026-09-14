package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SaveAvaliacaoPrestadorCommandHandler implements CommandHandler<SaveAvaliacaoPrestadorCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveAvaliacaoPrestadorCommandHandler.class);

   private final MissaoProcessoServiceWrite missaoProcessoServiceWrite;

   public SaveAvaliacaoPrestadorCommandHandler(MissaoProcessoServiceWrite missaoProcessoServiceWrite) {
      this.missaoProcessoServiceWrite = missaoProcessoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveAvaliacaoPrestadorCommand command) {

      LOGGER.debug("SaveAvaliacaoPrestadorCommand : {}", command);

      return missaoProcessoServiceWrite.salvarAvaliacao(command);
   }

}
