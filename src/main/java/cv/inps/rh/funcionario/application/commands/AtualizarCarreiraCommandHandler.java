package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AtualizarCarreiraCommandHandler implements CommandHandler<AtualizarCarreiraCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtualizarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public AtualizarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(AtualizarCarreiraCommand command) {

    LOGGER.debug("AtualizarCarreiraCommand: {}", command);

    return ResponseEntity.ok(
        carreiraWriteService.atualizarCarreira(command.getCarreiraId(), command.getFuncionarioId(), command.getDadoscontratuaisreq()));
  }

}
