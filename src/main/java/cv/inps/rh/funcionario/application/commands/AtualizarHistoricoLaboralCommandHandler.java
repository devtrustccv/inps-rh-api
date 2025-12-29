package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;

@Component
public class AtualizarHistoricoLaboralCommandHandler implements CommandHandler<AtualizarHistoricoLaboralCommand, ResponseEntity<ValidarNovoHistoricoLaboralDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AtualizarHistoricoLaboralCommandHandler.class);

   public AtualizarHistoricoLaboralCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarNovoHistoricoLaboralDTO> handle(AtualizarHistoricoLaboralCommand command) {

      LOGGER.debug("AtualizarHistoricoLaboralCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}