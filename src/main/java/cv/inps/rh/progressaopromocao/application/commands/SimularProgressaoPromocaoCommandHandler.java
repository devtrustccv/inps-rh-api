package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.engine.CarreiraEvolucaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SimularProgressaoPromocaoCommandHandler implements CommandHandler<SimularProgressaoPromocaoCommand, ResponseEntity<String>> {

  private final CarreiraEvolucaoService carreiraEvolucaoService;

  public SimularProgressaoPromocaoCommandHandler(CarreiraEvolucaoService carreiraEvolucaoService) {
    this.carreiraEvolucaoService = carreiraEvolucaoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SimularProgressaoPromocaoCommand command) {

    carreiraEvolucaoService.executarSimulacao();

    return ResponseEntity.ok().build();
  }

}
