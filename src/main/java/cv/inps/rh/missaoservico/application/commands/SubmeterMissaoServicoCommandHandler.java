package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceWrite;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SubmeterMissaoServicoCommandHandler
      implements CommandHandler<SubmeterMissaoServicoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SubmeterMissaoServicoCommandHandler.class);

   private final MissaoServicoServiceWrite missaoServicoService;

   public SubmeterMissaoServicoCommandHandler(MissaoServicoServiceWrite missaoServicoService) {
      this.missaoServicoService = missaoServicoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SubmeterMissaoServicoCommand command) {

      LOGGER.debug("SubmeterMissaoServicoCommand : {}", command);

      return missaoServicoService.submeter(command);
   }

}
