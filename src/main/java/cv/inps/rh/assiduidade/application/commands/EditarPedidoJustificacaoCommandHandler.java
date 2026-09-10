package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.JustificarFaltaWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditarPedidoJustificacaoCommandHandler implements CommandHandler<EditarPedidoJustificacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EditarPedidoJustificacaoCommandHandler.class);

   private final JustificarFaltaWriteService justificarFaltaService;
   public EditarPedidoJustificacaoCommandHandler(JustificarFaltaWriteService justificarFaltaService) {

     this.justificarFaltaService = justificarFaltaService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(EditarPedidoJustificacaoCommand command) {

      LOGGER.debug("EditarPedidoJustificacaoCommand : {}", command);


      return ResponseEntity.ok(justificarFaltaService.editarPedidoJustificacao(command));
   }

}
