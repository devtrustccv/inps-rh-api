package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MudarEstadoColaboradorCommandHandler implements CommandHandler<MudarEstadoColaboradorCommand, ResponseEntity<AtivarInativarColaboradorDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MudarEstadoColaboradorCommandHandler.class);

   public MudarEstadoColaboradorCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<AtivarInativarColaboradorDTO> handle(MudarEstadoColaboradorCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}
