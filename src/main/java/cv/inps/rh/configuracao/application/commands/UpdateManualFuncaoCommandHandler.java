package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.ManualFuncaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateManualFuncaoCommandHandler implements CommandHandler<UpdateManualFuncaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateManualFuncaoCommandHandler.class);

   private final ManualFuncaoService manualFuncaoService;

   public UpdateManualFuncaoCommandHandler(ManualFuncaoService manualFuncaoService) {
      this.manualFuncaoService = manualFuncaoService;

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateManualFuncaoCommand command) {

      LOGGER.debug("UpdateManualFuncaoCommand : {}", command);

      return manualFuncaoService.atualizar(command);
   }

}
