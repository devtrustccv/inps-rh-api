package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.service.VinculoMovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class CriarVinculoMovimentoCommandHandler implements CommandHandler<CriarVinculoMovimentoCommand, ResponseEntity<VinculoMovimentoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CriarVinculoMovimentoCommandHandler.class);

  private final VinculoMovimentoService vinculoMovimentoService;

  public CriarVinculoMovimentoCommandHandler(VinculoMovimentoService vinculoMovimentoService) {
    this.vinculoMovimentoService = vinculoMovimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<VinculoMovimentoResponseDTO> handle(CriarVinculoMovimentoCommand command) {

    LOGGER.debug("CriarVinculoMovimentoCommand: {}", command);

    var data = vinculoMovimentoService.criar(command.getVinculoId(), command.getDto());

    return ResponseEntity.status(HttpStatus.CREATED).body(data);
  }

}
