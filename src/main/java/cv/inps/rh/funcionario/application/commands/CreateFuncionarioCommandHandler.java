package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.RegistarColaboradorService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

  private final RegistarColaboradorService dossierService;

  public CreateFuncionarioCommandHandler(RegistarColaboradorService dossierService) {
    this.dossierService = dossierService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(CreateFuncionarioCommand command) {

    LOGGER.debug("Handling CreateFuncionarioCommand: {}", command);

    return ResponseEntity.ok(dossierService.saveDossierColaborador(command));

  }

}
