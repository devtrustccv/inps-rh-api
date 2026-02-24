package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.configuracao.domain.service.FeriadoService;
import lombok.RequiredArgsConstructor;

@Component
public class DeleteFeriadoCommandHandler implements CommandHandler<DeleteFeriadoCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFeriadoCommandHandler.class);

   private final FeriadoService feriadoService;

   public DeleteFeriadoCommandHandler(FeriadoService feriadoService) { this.feriadoService = feriadoService; }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(DeleteFeriadoCommand command) {

      LOGGER.debug("DeleteFeriadoCommand : {}", command);

      feriadoService.delete(command.getIdFeriado());

      return ResponseEntity.ok().build();
   }

}
