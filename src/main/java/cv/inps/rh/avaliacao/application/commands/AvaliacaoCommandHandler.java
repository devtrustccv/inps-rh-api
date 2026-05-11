package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.ProcessoAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AvaliacaoCommandHandler implements CommandHandler<AvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AvaliacaoCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public AvaliacaoCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AvaliacaoCommand command) {

      LOGGER.debug("AvaliacaoCommand : {}", command);

      return processoAvaliacaoService.gravarAvaliacao(command.getUuid(), command.getAvaliacao());
   }

}
