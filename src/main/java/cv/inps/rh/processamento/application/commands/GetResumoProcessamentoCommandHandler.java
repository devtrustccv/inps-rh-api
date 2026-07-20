package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetResumoProcessamentoCommandHandler
    implements CommandHandler<GetResumoProcessamentoCommand, ResponseEntity<ResumoProcessamentoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetResumoProcessamentoCommandHandler.class);

  private final ProcessamentoSalarialReadService processamentoSalarialService;

  public GetResumoProcessamentoCommandHandler(ProcessamentoSalarialReadService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<ResumoProcessamentoDTO> handle(GetResumoProcessamentoCommand command) {

    LOGGER.debug("GetResumoProcessamentoCommand: {}", command);

    var data = processamentoSalarialService.getResumoProcessamentoSalarial(command.getProcessamentoids().id());

    return ResponseEntity.ok(data);
  }
}
