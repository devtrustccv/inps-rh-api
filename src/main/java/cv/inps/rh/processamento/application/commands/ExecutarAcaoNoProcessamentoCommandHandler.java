package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialWriteService;
import cv.inps.rh.shared.application.dto.ProcedureMsgDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExecutarAcaoNoProcessamentoCommandHandler implements CommandHandler<ExecutarAcaoNoProcessamentoCommand, ResponseEntity<ProcedureMsgDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecutarAcaoNoProcessamentoCommandHandler.class);

  private final ProcessamentoSalarialWriteService service;

  public ExecutarAcaoNoProcessamentoCommandHandler(ProcessamentoSalarialWriteService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<ProcedureMsgDTO> handle(ExecutarAcaoNoProcessamentoCommand command) {

    LOGGER.debug("ExecutarAcaoNoProcessamentoCommand : {}", command);

    var ids = command.getProcessamentoactionrequest().idsProcessamento();

    var action = command.getProcessamentoactionrequest().action();

    var pms = new ProcedureMsgDTO();

    if (action.equals(ProcessamentoSalarialAction.ELIMINAR_PROCESSAMENTO)) {
      pms.setMessage(service.eliminarProcessamento(ids));
      return ResponseEntity.ok(pms);
    }

    switch (action) {
      case VALIDAR -> service.validar(ids, command.getProcessamentoactionrequest().tipoValidacao());
      case CABIMENTAR -> service.cabimentar(ids);
      case ELIMINAR_CABIMENTO -> service.eliminarCabimento(ids);
      case AUTORIZAR -> service.autorizar(ids);
      case RETROCEDER -> service.retroceder(ids);
      default -> throw new IllegalArgumentException("Invalid option");
    }

    return ResponseEntity.ok(pms);
  }

}
