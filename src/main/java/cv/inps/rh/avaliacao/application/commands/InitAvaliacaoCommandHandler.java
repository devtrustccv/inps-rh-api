package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class InitAvaliacaoCommandHandler implements CommandHandler<InitAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

  //Registo de Objectivos por Colaborador

   private static final Logger LOGGER = LoggerFactory.getLogger(InitAvaliacaoCommandHandler.class);

   private final AvaliacaoService avaliacaoService;

   public InitAvaliacaoCommandHandler(AvaliacaoService avaliacaoService) {
      this.avaliacaoService = avaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(InitAvaliacaoCommand command) {

      LOGGER.debug("InitAvaliacaoCommand : {}", command);

      return avaliacaoService.initAvaliacao(command);
   }

}
