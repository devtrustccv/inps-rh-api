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
public class ProcessoParecerColaboradorCommandHandler implements CommandHandler<ProcessoParecerColaboradorCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoParecerColaboradorCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public ProcessoParecerColaboradorCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ProcessoParecerColaboradorCommand command) {

      LOGGER.debug("ProcessoParecerColaboradorCommand : {}", command);

      return ResponseEntity.ok(processoAvaliacaoService.gravarParecerColaborador(command.getUuid(), command.getParecercolaborador()));
   }

}
