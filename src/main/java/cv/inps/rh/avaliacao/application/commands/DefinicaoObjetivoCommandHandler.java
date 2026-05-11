package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefinicaoObjetivoCommandHandler implements CommandHandler<DefinicaoObjetivoCommand, ResponseEntity<Map<String, ?>>> {

  //Registo de Objectivos por Colaborador

   private static final Logger LOGGER = LoggerFactory.getLogger(DefinicaoObjetivoCommandHandler.class);

   private final AvaliacaoService avaliacaoService;

   public DefinicaoObjetivoCommandHandler(AvaliacaoService avaliacaoService) {
      this.avaliacaoService = avaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(DefinicaoObjetivoCommand command) {

      LOGGER.debug("DefinicaoObjetivoCommand : {}", command);

      return avaliacaoService.definicaoObjetivos(command);
   }

}
