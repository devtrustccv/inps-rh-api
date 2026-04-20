package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.ComponenteAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateComponentesAvaliacaoCommandHandler implements CommandHandler<CreateComponentesAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateComponentesAvaliacaoCommandHandler.class);

   private final ComponenteAvaliacaoService componenteAvaliacaoService;

   public CreateComponentesAvaliacaoCommandHandler(ComponenteAvaliacaoService componenteAvaliacaoService) {
      this.componenteAvaliacaoService = componenteAvaliacaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(CreateComponentesAvaliacaoCommand command) {

      LOGGER.debug("CreateComponentesAvaliacaoCommand : {}", command);

      return componenteAvaliacaoService.registar(command);
   }

}
