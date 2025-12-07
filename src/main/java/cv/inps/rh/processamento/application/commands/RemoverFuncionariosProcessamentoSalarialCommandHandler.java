package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class RemoverFuncionariosProcessamentoSalarialCommandHandler implements CommandHandler<RemoverFuncionariosProcessamentoSalarialCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoverFuncionariosProcessamentoSalarialCommandHandler.class);

  private final ProcessamentoSalarialWriteService processamentoSalarialService;

  public RemoverFuncionariosProcessamentoSalarialCommandHandler(ProcessamentoSalarialWriteService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(RemoverFuncionariosProcessamentoSalarialCommand command) {

    LOGGER.debug("RemoverFuncionariosProcessamentoSalarialCommand : {}", command);

    processamentoSalarialService.removerFuncionariosProcessados(command.getMarcarnaoprocessadorequest().getFuncionarios());

    return ResponseEntity.ok().build();
  }

}
