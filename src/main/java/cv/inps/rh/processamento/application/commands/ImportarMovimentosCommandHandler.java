package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.MovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ImportarMovimentosCommandHandler implements CommandHandler<ImportarMovimentosCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportarMovimentosCommandHandler.class);
  private final MovimentoService movimentoService;

  public ImportarMovimentosCommandHandler(MovimentoService movimentoService) {

    this.movimentoService = movimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ImportarMovimentosCommand command) {

    LOGGER.debug("ImportarMovimentosCommand : {}", command);

    movimentoService.uploadMovement(command.getFicheiro());

    return ResponseEntity.ok().build();
  }

}
