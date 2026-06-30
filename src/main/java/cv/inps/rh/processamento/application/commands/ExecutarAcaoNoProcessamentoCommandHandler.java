package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExecutarAcaoNoProcessamentoCommandHandler implements CommandHandler<ExecutarAcaoNoProcessamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecutarAcaoNoProcessamentoCommandHandler.class);

  private final ProcessamentoSalarialWriteService service;

  public ExecutarAcaoNoProcessamentoCommandHandler(ProcessamentoSalarialWriteService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ExecutarAcaoNoProcessamentoCommand command) {

    LOGGER.debug("ExecutarAcaoNoProcessamentoCommand : {}", command);

    var ids = command.getProcessamentoactionrequest().idsProcessamento();

    var action = command.getProcessamentoactionrequest().action();

    if (action.equals(ProcessamentoSalarialAction.ELIMINAR_PROCESSAMENTO))
      return ResponseEntity.ok(service.eliminarProcessamento(ids));

    switch (action) {
      case VALIDAR -> service.validar(ids);
      case CABIMENTAR -> service.cabimentar(ids);
      case ELIMINAR_CABIMENTO -> service.extornarCabimento(ids);
      case AUTORIZAR -> service.autorizar(ids);
      default -> throw new IllegalArgumentException("Invalid option");
    }

    return ResponseEntity.ok().build();
  }

}
