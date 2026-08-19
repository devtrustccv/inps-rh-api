package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.MobilidadeWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveMobilidadeCommandHandler implements CommandHandler<SaveMobilidadeCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveMobilidadeCommandHandler.class);

  private final MobilidadeWriteService novaMobilidadeService;

  public SaveMobilidadeCommandHandler(MobilidadeWriteService novaMobilidadeService) {
    this.novaMobilidadeService = novaMobilidadeService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(SaveMobilidadeCommand command) {

    return ResponseEntity.ok(novaMobilidadeService.save(command));
  }

}
