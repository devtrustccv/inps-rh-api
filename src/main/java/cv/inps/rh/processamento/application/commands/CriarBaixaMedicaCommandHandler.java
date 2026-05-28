package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.baixamedica.BaixaMedicaServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class CriarBaixaMedicaCommandHandler implements CommandHandler<CriarBaixaMedicaCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CriarBaixaMedicaCommandHandler.class);

  private final BaixaMedicaServiceWrite baixaMedicaServiceWrite;

  public CriarBaixaMedicaCommandHandler(BaixaMedicaServiceWrite baixaMedicaServiceWrite) {
    this.baixaMedicaServiceWrite = baixaMedicaServiceWrite;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(CriarBaixaMedicaCommand command) {

    LOGGER.debug("CriarBaixaMedicaCommand: {}", command);

    return ResponseEntity.ok(baixaMedicaServiceWrite.criar(command.getReq()));
  }

}
