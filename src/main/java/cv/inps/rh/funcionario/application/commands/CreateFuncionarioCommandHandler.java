package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.service.RegistarColaboradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

  private final RegistarColaboradorService dossierService;

  public CreateFuncionarioCommandHandler(RegistarColaboradorService dossierService) {
    this.dossierService = dossierService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(CreateFuncionarioCommand command) {

    LOGGER.debug("Handling CreateFuncionarioCommand: {}", command);

    return ResponseEntity.ok(dossierService.saveDossierColaborador(command));

  }

}
