package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoResponseDTO;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SalvarDadosInstituicaoCommandHandler implements
    CommandHandler<SalvarDadosInstituicaoCommand, ResponseEntity<DadosInstituicaoResponseDTO>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SalvarDadosInstituicaoCommandHandler.class);

  private final SoatService service;

  public SalvarDadosInstituicaoCommandHandler(SoatService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosInstituicaoResponseDTO> handle(
      SalvarDadosInstituicaoCommand command) {

    LOGGER.debug("Saving institution data");

    var result = service.salvarDadosInstituicao(command.getDadosInstituicaoRequest());
    return ResponseEntity.ok(result);
  }
}
