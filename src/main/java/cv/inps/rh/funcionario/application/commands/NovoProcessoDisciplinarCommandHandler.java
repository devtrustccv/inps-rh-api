package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ProcessoDisciplinarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class NovoProcessoDisciplinarCommandHandler implements CommandHandler<NovoProcessoDisciplinarCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoProcessoDisciplinarCommandHandler.class);

  private final ProcessoDisciplinarService processoDisciplinarService;

  public NovoProcessoDisciplinarCommandHandler(ProcessoDisciplinarService processoDisciplinarService) {
    this.processoDisciplinarService = processoDisciplinarService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(NovoProcessoDisciplinarCommand command) {

    LOGGER.info("Novo processo disciplinar para funcionario: {}", command);

    var id = processoDisciplinarService.saveNovoProcessoDisciplinar(command.getFuncionarioId(), command.getProcessodisciplinarrequest());

    return ResponseEntity.ok(id.toString());
  }

}
