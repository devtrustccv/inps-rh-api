package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.service.RegistarColaboradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<FuncionarioResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

  private final RegistarColaboradorService dossierService;

   public CreateFuncionarioCommandHandler(RegistarColaboradorService dossierService) {

     this.dossierService = dossierService;
   }

   @IgrpCommandHandler
   public ResponseEntity<FuncionarioResponseDTO> handle(CreateFuncionarioCommand command) {
     var dto = command.getFuncionariorequest();

     LOGGER.info("Iniciando criação de funcionário: {}", dto);

     return ResponseEntity.ok(dossierService.saveDossierColaborador(command));

   }

}
