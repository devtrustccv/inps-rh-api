package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidacaoPedidoDeclaracaoCommandHandler implements CommandHandler<ValidacaoPedidoDeclaracaoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoPedidoDeclaracaoCommandHandler.class);
   private final PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService;

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ValidacaoPedidoDeclaracaoCommand command) {

      LOGGER.debug("ValidacaoPedidoDeclaracaoCommand : {}", command);

      return ResponseEntity.ok(pedidoDeclaracaoWriteService.validarPedido(command));
   }

}
