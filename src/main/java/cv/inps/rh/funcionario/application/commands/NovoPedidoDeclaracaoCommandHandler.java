package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.documento.PedidoDeclaracaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NovoPedidoDeclaracaoCommandHandler implements CommandHandler<NovoPedidoDeclaracaoCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoPedidoDeclaracaoCommandHandler.class);

  private final PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService;

  public NovoPedidoDeclaracaoCommandHandler(PedidoDeclaracaoWriteService pedidoDeclaracaoWriteService) {
    this.pedidoDeclaracaoWriteService = pedidoDeclaracaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(NovoPedidoDeclaracaoCommand command) {

    LOGGER.debug("NovoPedidoDeclaracaoCommand : {}", command);

    var response =
        pedidoDeclaracaoWriteService.saveNovoPedido(command);


    return ResponseEntity.ok(response);
  }

}
