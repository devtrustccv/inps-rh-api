package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AtualizarCarreiraCommandHandler implements CommandHandler<AtualizarCarreiraCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtualizarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public AtualizarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AtualizarCarreiraCommand command) {

    LOGGER.debug("AtualizarCarreiraCommand: {}", command);

    carreiraWriteService.atualizarCarreira(command.getCarreiraId(), command.getFuncionarioId(), command.getDadoscontratuaisreq());

    return ResponseEntity.ok().build();
  }

}
