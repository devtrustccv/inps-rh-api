package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoAvaliacoesGravadasResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveMissaoAvaliacoesCommandHandler implements CommandHandler<SaveMissaoAvaliacoesCommand, ResponseEntity<MissaoAvaliacoesGravadasResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoAvaliacoesCommandHandler.class);

  private final MissaoProcessoServiceWrite missaoProcessoServiceWrite;

  public SaveMissaoAvaliacoesCommandHandler(MissaoProcessoServiceWrite missaoProcessoServiceWrite) {
    this.missaoProcessoServiceWrite = missaoProcessoServiceWrite;
  }

   @IgrpCommandHandler
   public ResponseEntity<MissaoAvaliacoesGravadasResponseDTO> handle(SaveMissaoAvaliacoesCommand command) {

      LOGGER.debug("SaveMissaoAvaliacoesCommand : {}", command);

      return missaoProcessoServiceWrite.salvarAvaliacoesDaMissao(command);
   }

}
