package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.service.VinculoMovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SyncVinculoMovimentosCommandHandler implements CommandHandler<SyncVinculoMovimentosCommand, ResponseEntity<List<VinculoMovimentoResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SyncVinculoMovimentosCommandHandler.class);

  private final VinculoMovimentoService vinculoMovimentoService;

  public SyncVinculoMovimentosCommandHandler(VinculoMovimentoService vinculoMovimentoService) {
    this.vinculoMovimentoService = vinculoMovimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<List<VinculoMovimentoResponseDTO>> handle(SyncVinculoMovimentosCommand command) {

    LOGGER.debug("SyncVinculoMovimentosCommand: {}", command);

    var data = vinculoMovimentoService.syncMovimentos(command.getVinculoId(), command.getMovimentos());

    return ResponseEntity.ok(data);
  }

}
