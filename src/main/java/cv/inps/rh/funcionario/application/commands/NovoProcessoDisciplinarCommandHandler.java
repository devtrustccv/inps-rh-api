package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.processodisciplinar.ProcessoDisciplinarWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class NovoProcessoDisciplinarCommandHandler implements CommandHandler<NovoProcessoDisciplinarCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoProcessoDisciplinarCommandHandler.class);

  private final ProcessoDisciplinarWriteService processoDisciplinarService;

  public NovoProcessoDisciplinarCommandHandler(ProcessoDisciplinarWriteService processoDisciplinarService) {
    this.processoDisciplinarService = processoDisciplinarService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(NovoProcessoDisciplinarCommand command) {

    LOGGER.info("Novo processo disciplinar para funcionario: {}", command);

    return ResponseEntity.ok(
        processoDisciplinarService.saveNovoProcessoDisciplinar(command.getFuncionarioId(), command.getProcessodisciplinarrequest()));
  }

}
