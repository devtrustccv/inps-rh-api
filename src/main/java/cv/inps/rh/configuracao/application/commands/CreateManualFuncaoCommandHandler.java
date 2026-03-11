package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.ManualFuncaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class CreateManualFuncaoCommandHandler implements CommandHandler<CreateManualFuncaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateManualFuncaoCommandHandler.class);

   private final ManualFuncaoService manualFuncaoService;

   public CreateManualFuncaoCommandHandler(ManualFuncaoService manualFuncaoService) {
      this.manualFuncaoService = manualFuncaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(CreateManualFuncaoCommand command) {

      LOGGER.debug("CreateManualFuncaoCommand : {}", command);

      return manualFuncaoService.registar(command);
   }

}
