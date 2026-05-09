package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.FeriaWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidarPedidoFeriaCommandHandler implements CommandHandler<ValidarPedidoFeriaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarPedidoFeriaCommandHandler.class);

  private final FeriaWriteService feriaWriteService;

   public ValidarPedidoFeriaCommandHandler(FeriaWriteService feriaWriteService) {

     this.feriaWriteService = feriaWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarPedidoFeriaCommand command) {

      LOGGER.debug("ValidarPedidoFeriaCommand : {}", command);


      return ResponseEntity.ok(feriaWriteService.validarFeria(command));
   }

}
