package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NovoPedidoDeclaracaoCommandHandler implements CommandHandler<NovoPedidoDeclaracaoCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoPedidoDeclaracaoCommandHandler.class);

  private final PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService;

  public NovoPedidoDeclaracaoCommandHandler(PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService) {
    this.pedidoDeclaracaoWriteService = pedidoDeclaracaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(NovoPedidoDeclaracaoCommand command) {

    LOGGER.debug("NovoPedidoDeclaracaoCommand : {}", command);

    return ResponseEntity.ok(pedidoDeclaracaoWriteService.saveNovoPedido(command));
  }

}
