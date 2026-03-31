package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfirmarProgressaoCommandHandler implements CommandHandler<ConfirmarProgressaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmarProgressaoCommandHandler.class);

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public ConfirmarProgressaoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ConfirmarProgressaoCommand command) {

    LOGGER.debug("ConfirmarProgressaoCommand : {}", command);

    progressaoPromocaoWriteService.confirmar(Long.valueOf(command.getValidacaoId()));

    return ResponseEntity.ok().build();
  }
}
