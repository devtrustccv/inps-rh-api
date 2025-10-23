package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<FuncionarioResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

   public CreateFuncionarioCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<FuncionarioResponseDTO> handle(CreateFuncionarioCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}