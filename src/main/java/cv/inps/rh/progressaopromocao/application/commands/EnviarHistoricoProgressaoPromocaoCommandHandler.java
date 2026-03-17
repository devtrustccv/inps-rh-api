package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EnviarHistoricoProgressaoPromocaoCommandHandler implements CommandHandler<EnviarHistoricoProgressaoPromocaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnviarHistoricoProgressaoPromocaoCommandHandler.class);

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public EnviarHistoricoProgressaoPromocaoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(EnviarHistoricoProgressaoPromocaoCommand command) {

    LOGGER.debug("EnviarHistoricoProgressaoPromocaoCommand : {}", command);

    progressaoPromocaoWriteService.sendToHistory(command.getHistoricoids().getIds());

    return ResponseEntity.ok().build();
  }

}
