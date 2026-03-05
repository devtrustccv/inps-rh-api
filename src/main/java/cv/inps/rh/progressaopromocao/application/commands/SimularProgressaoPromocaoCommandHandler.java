package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.engine.CarreiraEvolucaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SimularProgressaoPromocaoCommandHandler implements CommandHandler<SimularProgressaoPromocaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimularProgressaoPromocaoCommandHandler.class);

  private final CarreiraEvolucaoService carreiraEvolucaoService;

  public SimularProgressaoPromocaoCommandHandler(CarreiraEvolucaoService carreiraEvolucaoService) {
    this.carreiraEvolucaoService = carreiraEvolucaoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SimularProgressaoPromocaoCommand command) {

    LOGGER.debug("SIMULATION STARTED");

    carreiraEvolucaoService.executarSimulacao();

    return ResponseEntity.ok().build();
  }

}
