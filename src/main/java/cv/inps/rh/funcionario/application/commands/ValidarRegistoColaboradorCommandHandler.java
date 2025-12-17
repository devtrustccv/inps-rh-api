package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidarRegistoColaboradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidarRegistoColaboradorCommandHandler implements CommandHandler<ValidarRegistoColaboradorCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRegistoColaboradorCommandHandler.class);

  private final ValidarRegistoColaboradorService dossierService;

  public ValidarRegistoColaboradorCommandHandler(ValidarRegistoColaboradorService dossierService) {

    this.dossierService = dossierService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(ValidarRegistoColaboradorCommand command) {


    LOGGER.info("Iniciando atualização/validacao de funcionário: {}", command);

    return ResponseEntity.ok(dossierService.validarRegistoColaborador(command));

  }


}
