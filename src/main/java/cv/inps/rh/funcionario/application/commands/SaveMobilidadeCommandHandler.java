package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.NovaMobilidadeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveMobilidadeCommandHandler implements CommandHandler<SaveMobilidadeCommand, ResponseEntity<MobilidadeDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveMobilidadeCommandHandler.class);

  private final NovaMobilidadeService novaMobilidadeService;

  public SaveMobilidadeCommandHandler(NovaMobilidadeService novaMobilidadeService) {
    this.novaMobilidadeService = novaMobilidadeService;
  }

  @IgrpCommandHandler
  public ResponseEntity<MobilidadeDTO> handle(SaveMobilidadeCommand command) {

    return ResponseEntity.ok(novaMobilidadeService.save(command));
  }

}
