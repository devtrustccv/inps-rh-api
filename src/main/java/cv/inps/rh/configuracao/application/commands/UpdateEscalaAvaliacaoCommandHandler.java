package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.EscalaAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateEscalaAvaliacaoCommandHandler implements CommandHandler<UpdateEscalaAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEscalaAvaliacaoCommandHandler.class);

   private final EscalaAvaliacaoService escalaAvaliacaoService;

   public UpdateEscalaAvaliacaoCommandHandler(EscalaAvaliacaoService escalaAvaliacaoService) {
      this.escalaAvaliacaoService = escalaAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateEscalaAvaliacaoCommand command) {

      LOGGER.debug("UpdateEscalaAvaliacaoCommand : {}", command);

      return escalaAvaliacaoService.atualizar(command);
   }

}
