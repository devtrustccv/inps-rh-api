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
public class SaveProcessoAutorizacaoCommandHandler implements CommandHandler<SaveProcessoAutorizacaoCommand, ResponseEntity<ProcessoEtapaGravadaResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveProcessoAutorizacaoCommandHandler.class);

   private final MissaoProcessoServiceWrite missaoProcessoServiceWrite;

   public SaveProcessoAutorizacaoCommandHandler(MissaoProcessoServiceWrite missaoProcessoServiceWrite) {
      this.missaoProcessoServiceWrite = missaoProcessoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> handle(SaveProcessoAutorizacaoCommand command) {

      LOGGER.debug("SaveProcessoAutorizacaoCommand : {}", command);

      return missaoProcessoServiceWrite.salvarAutorizacao(command);
   }

}
