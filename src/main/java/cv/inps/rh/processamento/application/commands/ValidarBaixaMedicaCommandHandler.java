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
public class ValidarBaixaMedicaCommandHandler implements CommandHandler<ValidarBaixaMedicaCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarBaixaMedicaCommandHandler.class);

  private final BaixaMedicaServiceWrite baixaMedicaServiceWrite;

  public ValidarBaixaMedicaCommandHandler(BaixaMedicaServiceWrite baixaMedicaServiceWrite) {
    this.baixaMedicaServiceWrite = baixaMedicaServiceWrite;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(ValidarBaixaMedicaCommand command) {

    LOGGER.debug("ValidarBaixaMedicaCommand: {}", command);

    return ResponseEntity.ok(baixaMedicaServiceWrite.validar(command.getPedidoId(), command.getValidar(), command.getAjuste()));
  }

}
