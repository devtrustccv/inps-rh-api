package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateSoatDetalhesCommandHandler implements CommandHandler<UpdateSoatDetalhesCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSoatDetalhesCommandHandler.class);

  private final SoatService soatService;

  public UpdateSoatDetalhesCommandHandler(SoatService soatService) {
    this.soatService = soatService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(UpdateSoatDetalhesCommand command) {

    LOGGER.debug("UpdateSoatDetalhesCommand : {}", command);

    soatService.updateDetalhesSoat(command.getDetalhes());

    return ResponseEntity.ok().build();
  }

}
