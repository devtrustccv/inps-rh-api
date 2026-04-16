package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class NovoSeguradoCommandHandler implements CommandHandler<NovoSeguradoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoSeguradoCommandHandler.class);

  private final FosService fosService;

  public NovoSeguradoCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(NovoSeguradoCommand command) {

    LOGGER.debug("NovoSeguradoCommand : {}", command);

    fosService.novosFos(command.getAno(), command.getMes());

    return ResponseEntity.ok().build();
  }

}
