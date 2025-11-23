package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class NovaCarreiraCommandHandler implements CommandHandler<NovaCarreiraCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovaCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public NovaCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(NovaCarreiraCommand command) {

    LOGGER.info("Nova carreira para funcionario: {}", command);

    carreiraWriteService.novaCarreira(command.getFuncionarioId(), command.getDadoscontratuaisreq());

    return ResponseEntity.ok("Carreira cadastrada com sucesso!");
  }

}
