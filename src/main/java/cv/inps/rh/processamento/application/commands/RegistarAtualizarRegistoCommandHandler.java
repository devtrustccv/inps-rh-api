package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistarAtualizarRegistoCommandHandler implements CommandHandler<RegistarAtualizarRegistoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegistarAtualizarRegistoCommandHandler.class);

  private final FosService fosService;

  public RegistarAtualizarRegistoCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(RegistarAtualizarRegistoCommand command) {

    LOGGER.debug("RegistarAtualizarRegistoCommand : {}", command);

    fosService.gravarNovaLinhaFos(command.getDetalhexmlrequest());

    return ResponseEntity.ok().build();
  }

}
