package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.service.VinculoMovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EliminarVinculoMovimentoCommandHandler implements CommandHandler<EliminarVinculoMovimentoCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EliminarVinculoMovimentoCommandHandler.class);

  private final VinculoMovimentoService vinculoMovimentoService;

  public EliminarVinculoMovimentoCommandHandler(VinculoMovimentoService vinculoMovimentoService) {
    this.vinculoMovimentoService = vinculoMovimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(EliminarVinculoMovimentoCommand command) {

    LOGGER.debug("EliminarVinculoMovimentoCommand: {}", command);

    vinculoMovimentoService.eliminar(command.getVinculoId(), command.getId());

    return ResponseEntity.noContent().build();
  }

}
