package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AdiantamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerificarPedidoAdiantamentoCommandHandler implements CommandHandler<VerificarPedidoAdiantamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificarPedidoAdiantamentoCommandHandler.class);

  private final AdiantamentoService service;

  public VerificarPedidoAdiantamentoCommandHandler(AdiantamentoService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(VerificarPedidoAdiantamentoCommand command) {

    LOGGER.debug("VerificarPedidoAdiantamentoCommand : {}", command);

    service.verificar(command.getEmprestimoId(), command.getVerificaradiantamentorequest());

    return ResponseEntity.ok().build();
  }

}
