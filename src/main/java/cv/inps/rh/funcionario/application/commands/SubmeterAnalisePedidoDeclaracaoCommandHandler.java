package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoWriteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubmeterAnalisePedidoDeclaracaoCommandHandler implements CommandHandler<SubmeterAnalisePedidoDeclaracaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SubmeterAnalisePedidoDeclaracaoCommandHandler.class);
   private final PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService;

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SubmeterAnalisePedidoDeclaracaoCommand command) {

      LOGGER.debug("SubmeterAnalisePedidoDeclaracaoCommand : {}", command);

      Map<String, ?> response = pedidoDeclaracaoWriteService.submeterAnalise(command);

      return ResponseEntity.ok(response);
   }

}
