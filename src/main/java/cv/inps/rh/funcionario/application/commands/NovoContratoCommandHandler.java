package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;

@Component
public class NovoContratoCommandHandler implements CommandHandler<NovoContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(NovoContratoCommandHandler.class);

   public NovoContratoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<DadosContratuaisRespDTO> handle(NovoContratoCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}