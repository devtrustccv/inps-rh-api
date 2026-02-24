package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.MovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarMovimentoImportadoCommandHandler implements CommandHandler<ValidarMovimentoImportadoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarMovimentoImportadoCommandHandler.class);

  private final MovimentoService movimentoService;

  public ValidarMovimentoImportadoCommandHandler(MovimentoService movimentoService) {
    this.movimentoService = movimentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarMovimentoImportadoCommand command) {

    LOGGER.debug("ValidarMovimentoImportadoCommand : {}", command);

    movimentoService.validarMovimento(command.getMovimentoId(), command.getValidacaomovimentoimportado());

    return ResponseEntity.ok().build();
  }

}
