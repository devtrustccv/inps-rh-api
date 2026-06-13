package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.service.VinculoMovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EditarVinculoMovimentoCommandHandler implements CommandHandler<EditarVinculoMovimentoCommand, ResponseEntity<VinculoMovimentoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EditarVinculoMovimentoCommandHandler.class);

  private final VinculoMovimentoService vinculoMovimentoService;

  public EditarVinculoMovimentoCommandHandler(VinculoMovimentoService vinculoMovimentoService) {
    this.vinculoMovimentoService = vinculoMovimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<VinculoMovimentoResponseDTO> handle(EditarVinculoMovimentoCommand command) {

    LOGGER.debug("EditarVinculoMovimentoCommand: {}", command);

    var data = vinculoMovimentoService.editar(command.getVinculoId(), command.getId(), command.getDto());

    return ResponseEntity.ok(data);
  }

}
