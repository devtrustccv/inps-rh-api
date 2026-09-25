package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.avaliacao.application.services.ObjectivosComunsService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AvaliacaoObjectivosComunsCommandHandler implements CommandHandler<AvaliacaoObjectivosComunsCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AvaliacaoObjectivosComunsCommandHandler.class);

   private final ObjectivosComunsService objectivosComunsService;

   public AvaliacaoObjectivosComunsCommandHandler(ObjectivosComunsService objectivosComunsService) {
      this.objectivosComunsService = objectivosComunsService;

   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(AvaliacaoObjectivosComunsCommand command) {

      LOGGER.debug("AvaliacaoObjectivosComunsCommand : {}", command);

      return ResponseEntity.ok(objectivosComunsService.avaliar(
          command.getAno(), command.getPeriodicidade(), command.getAvaliacaoobjectivoscomuns()));
   }

}
