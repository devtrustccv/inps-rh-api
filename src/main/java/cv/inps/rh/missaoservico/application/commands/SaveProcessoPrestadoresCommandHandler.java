package cv.inps.rh.missaoservico.application.commands;

import cv.inps.rh.missaoservico.application.dto.ProcessoEtapaGravadaResponseDTO;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveProcessoPrestadoresCommandHandler implements CommandHandler<SaveProcessoPrestadoresCommand, ResponseEntity<ProcessoEtapaGravadaResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveProcessoPrestadoresCommandHandler.class);

   private final MissaoProcessoServiceWrite missaoProcessoServiceWrite;

   public SaveProcessoPrestadoresCommandHandler(MissaoProcessoServiceWrite missaoProcessoServiceWrite) {
      this.missaoProcessoServiceWrite = missaoProcessoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> handle(SaveProcessoPrestadoresCommand command) {

      LOGGER.debug("SaveProcessoPrestadoresCommand : {}", command);

      return missaoProcessoServiceWrite.salvarPrestadores(command);
   }

}
