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
public class ProcessoObservacaoGeralCommandHandler implements CommandHandler<ProcessoObservacaoGeralCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoObservacaoGeralCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public ProcessoObservacaoGeralCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ProcessoObservacaoGeralCommand command) {

      LOGGER.debug("ProcessoObservacaoGeralCommand : {}", command);

      return ResponseEntity.ok(processoAvaliacaoService.gravarObservacaoGeral(command.getUuid(), command.getObservacaogeral()));
   }

}
