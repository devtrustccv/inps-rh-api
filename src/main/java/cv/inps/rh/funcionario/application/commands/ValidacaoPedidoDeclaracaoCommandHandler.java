package cv.inps.rh.funcionario.application.commands;

import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoWriteService;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ValidacaoPedidoDeclaracaoCommandHandler implements CommandHandler<ValidacaoPedidoDeclaracaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoPedidoDeclaracaoCommandHandler.class);
   private final PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService;

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidacaoPedidoDeclaracaoCommand command) {

      LOGGER.debug("ValidacaoPedidoDeclaracaoCommand : {}", command);

      Map<String, ?> response = pedidoDeclaracaoWriteService.validarPedido(command);

      return ResponseEntity.ok(response);
   }

}