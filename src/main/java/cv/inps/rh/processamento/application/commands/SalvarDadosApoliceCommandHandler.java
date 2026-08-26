package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.application.dto.DadosApoliceResponseDTO;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SalvarDadosApoliceCommandHandler implements
    CommandHandler<SalvarDadosApoliceCommand, ResponseEntity<DadosApoliceResponseDTO>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SalvarDadosApoliceCommandHandler.class);

  private final SoatService service;

  public SalvarDadosApoliceCommandHandler(SoatService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosApoliceResponseDTO> handle(SalvarDadosApoliceCommand command) {
    LOGGER.debug("Saving insurance policy data");

    return ResponseEntity.ok(
        service.salvarDadosApolice(command.getDadosApoliceRequest()));
  }
}
