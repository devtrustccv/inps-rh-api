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
public class ProcessoComissaoExecutivaCommandHandler implements CommandHandler<ProcessoComissaoExecutivaCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoComissaoExecutivaCommandHandler.class);

   private final ProcessoAvaliacaoService processoAvaliacaoService;

   public ProcessoComissaoExecutivaCommandHandler(ProcessoAvaliacaoService processoAvaliacaoService) {
      this.processoAvaliacaoService = processoAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ProcessoComissaoExecutivaCommand command) {

      LOGGER.debug("ProcessoComissaoExecutivaCommand : {}", command);

      return (processoAvaliacaoService.gravarComissaoExecutiva(command.getUuid(), command.getPeriodicidade(), command.getComissaoexecutiva()));
   }

}
