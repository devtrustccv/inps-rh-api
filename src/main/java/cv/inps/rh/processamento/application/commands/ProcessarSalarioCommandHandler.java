package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialWriteService;
import cv.inps.rh.shared.application.dto.ProcedureMsgDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ProcessarSalarioCommandHandler implements CommandHandler<ProcessarSalarioCommand, ResponseEntity<ProcedureMsgDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessarSalarioCommandHandler.class);

  private final ProcessamentoSalarialWriteService processamentoSalarialService;

  public ProcessarSalarioCommandHandler(ProcessamentoSalarialWriteService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<ProcedureMsgDTO> handle(ProcessarSalarioCommand command) {

    LOGGER.debug("ProcessarSalarioCommand : {}", command);

    var msg = processamentoSalarialService.processarSalario(command.getProcessamentosalariorequest());

    return ResponseEntity.ok(new ProcedureMsgDTO(msg));
  }

}
