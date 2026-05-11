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
public class AutoAvaliacaoCommandHandler
      implements CommandHandler<AutoAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AutoAvaliacaoCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public AutoAvaliacaoCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AutoAvaliacaoCommand command) {

      LOGGER.debug("AutoAvaliacaoCommand : {}", command);

      return processoAvaliacaoService.gravarAutoAvaliacao(command.getUuid(), command.getAvaliacao());
   }

}
