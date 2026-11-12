package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.ParamAjudaCustoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveConfiguracaoAjudaCustoCommandHandler implements CommandHandler<SaveConfiguracaoAjudaCustoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveConfiguracaoAjudaCustoCommandHandler.class);

  private final ParamAjudaCustoService paramAjudaCustoService;

  public SaveConfiguracaoAjudaCustoCommandHandler(ParamAjudaCustoService paramAjudaCustoService) {
    this.paramAjudaCustoService = paramAjudaCustoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveConfiguracaoAjudaCustoCommand command) {
    LOGGER.debug("SaveConfiguracaoAjudaCustoCommand: {}", command);
    return paramAjudaCustoService.guardar(command.getParamajudacusto());
  }

}
