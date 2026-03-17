package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.inps.rh.configuracao.application.services.ComponenteAvaliacaoService;

import java.util.Map;

@Component
public class UpdateComponenteAvaliacaoCommandHandler implements CommandHandler<UpdateComponenteAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateComponenteAvaliacaoCommandHandler.class);

   private final ComponenteAvaliacaoService componenteAvaliacaoService;

   public UpdateComponenteAvaliacaoCommandHandler(ComponenteAvaliacaoService componenteAvaliacaoService) {
      this.componenteAvaliacaoService = componenteAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateComponenteAvaliacaoCommand command) {

      LOGGER.debug("UpdateComponenteAvaliacaoCommand : {}", command);

      return componenteAvaliacaoService.atualizar(command);
   }

}
