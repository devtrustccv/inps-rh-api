package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.FeriaWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AlterarPedidoFeriaCommandHandler implements CommandHandler<AlterarPedidoFeriaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AlterarPedidoFeriaCommandHandler.class);

  private final FeriaWriteService feriaWriteService;

   public AlterarPedidoFeriaCommandHandler(FeriaWriteService feriaWriteService) {

     this.feriaWriteService = feriaWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AlterarPedidoFeriaCommand command) {

      LOGGER.debug("AlterarPedidoFeriaCommand : {}", command);


      return ResponseEntity.ok(feriaWriteService.alterarPedidoFeria(command));
   }

}
