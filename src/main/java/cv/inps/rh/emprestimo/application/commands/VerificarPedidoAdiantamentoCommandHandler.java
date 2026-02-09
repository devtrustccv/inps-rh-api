package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AdiantamentoEmprestimoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerificarPedidoAdiantamentoCommandHandler implements CommandHandler<VerificarPedidoAdiantamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificarPedidoAdiantamentoCommandHandler.class);

  private final AdiantamentoEmprestimoService service;

  public VerificarPedidoAdiantamentoCommandHandler(AdiantamentoEmprestimoService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(VerificarPedidoAdiantamentoCommand command) {

    LOGGER.debug("VerificarPedidoAdiantamentoCommand : {}", command);

    service.verificar(command.getEmprestimoId(), command.getBasedecisao());

    return ResponseEntity.ok().build();
  }

}
