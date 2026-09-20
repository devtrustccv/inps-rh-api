package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarEmprestimoCommandHandler implements CommandHandler<ValidarEmprestimoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarEmprestimoCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public ValidarEmprestimoCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarEmprestimoCommand command) {

    LOGGER.debug("ValidarEmprestimoCommand : {}", command);

    emprestimoWriteService.validarEmprestimo(command.getEmprestimoId(), command.getValidarEmprestimoRequest());

    return ResponseEntity.ok().build();
  }
}
