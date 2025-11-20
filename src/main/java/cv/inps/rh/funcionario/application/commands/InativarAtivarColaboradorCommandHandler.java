package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.InativarAtivarColaborarService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;

@Component
public class InativarAtivarColaboradorCommandHandler implements CommandHandler<InativarAtivarColaboradorCommand, ResponseEntity<AtivarInativarColaboradorDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(InativarAtivarColaboradorCommandHandler.class);

   private final InativarAtivarColaborarService inativarAtivarColaborarService;

   public InativarAtivarColaboradorCommandHandler(InativarAtivarColaborarService inativarAtivarColaborarService) {
      this.inativarAtivarColaborarService = inativarAtivarColaborarService;
   }

   @IgrpCommandHandler
   public ResponseEntity<AtivarInativarColaboradorDTO> handle(InativarAtivarColaboradorCommand command) {

     LOGGER.info("Iniciando inativacao/ativacao de funcionario: {}", command);

      return ResponseEntity.ok(inativarAtivarColaborarService.execute(command));
   }

}
