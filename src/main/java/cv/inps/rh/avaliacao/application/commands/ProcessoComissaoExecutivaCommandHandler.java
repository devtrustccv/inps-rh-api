package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.ProcessoAvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ProcessoComissaoExecutivaCommandHandler implements CommandHandler<ProcessoComissaoExecutivaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoComissaoExecutivaCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public ProcessoComissaoExecutivaCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ProcessoComissaoExecutivaCommand command) {

      LOGGER.debug("ProcessoComissaoExecutivaCommand : {}", command);

      return ResponseEntity.ok(processoAvaliacaoService.gravarComissaoExecutiva(command.getUuid(), command.getComissaoexecutiva()));
   }

}
