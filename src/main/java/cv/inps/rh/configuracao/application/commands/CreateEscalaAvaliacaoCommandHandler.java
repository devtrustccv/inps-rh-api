package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.EscalaAvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class CreateEscalaAvaliacaoCommandHandler implements CommandHandler<CreateEscalaAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateEscalaAvaliacaoCommandHandler.class);

   private final EscalaAvaliacaoService escalaAvaliacaoService;

   public CreateEscalaAvaliacaoCommandHandler(EscalaAvaliacaoService escalaAvaliacaoService) {
      this.escalaAvaliacaoService = escalaAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(CreateEscalaAvaliacaoCommand command) {

      LOGGER.debug("CreateEscalaAvaliacaoCommand : {}", command);

      return escalaAvaliacaoService.registar(command);
   }

}
