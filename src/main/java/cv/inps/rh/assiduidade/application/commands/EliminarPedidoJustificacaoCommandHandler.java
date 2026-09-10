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
public class EliminarPedidoJustificacaoCommandHandler implements CommandHandler<EliminarPedidoJustificacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EliminarPedidoJustificacaoCommandHandler.class);

   private final JustificarFaltaWriteService justificarFaltaService;
   public EliminarPedidoJustificacaoCommandHandler(JustificarFaltaWriteService justificarFaltaService) {

     this.justificarFaltaService = justificarFaltaService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(EliminarPedidoJustificacaoCommand command) {

      LOGGER.debug("EliminarPedidoJustificacaoCommand : {}", command);


      return ResponseEntity.ok(justificarFaltaService.eliminarPedidoJustificacao(command));
   }

}
