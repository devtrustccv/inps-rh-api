package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.ProcessoAvaliacaoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ProcessoObservacaoGeralCommandHandler implements CommandHandler<ProcessoObservacaoGeralCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoObservacaoGeralCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public ProcessoObservacaoGeralCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ProcessoObservacaoGeralCommand command) {

      LOGGER.debug("ProcessoObservacaoGeralCommand : {}", command);

      return (processoAvaliacaoService.gravarObservacaoGeral(command.getUuid(), command.getPeriodicidade(), command.getObservacaogeral()));
   }

}
