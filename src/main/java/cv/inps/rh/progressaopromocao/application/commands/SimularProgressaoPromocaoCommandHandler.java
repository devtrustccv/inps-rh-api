package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SimularProgressaoPromocaoCommandHandler implements CommandHandler<SimularProgressaoPromocaoCommand, ResponseEntity<String>> {

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public SimularProgressaoPromocaoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SimularProgressaoPromocaoCommand command) {

    progressaoPromocaoWriteService.simular();

    return ResponseEntity.ok().build();
  }

}
