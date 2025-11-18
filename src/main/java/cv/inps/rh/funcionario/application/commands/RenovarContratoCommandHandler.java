package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;

@Component
public class RenovarContratoCommandHandler implements CommandHandler<RenovarContratoCommand, ResponseEntity<RenovacaoContratoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(RenovarContratoCommandHandler.class);

   public RenovarContratoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<RenovacaoContratoDTO> handle(RenovarContratoCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}
