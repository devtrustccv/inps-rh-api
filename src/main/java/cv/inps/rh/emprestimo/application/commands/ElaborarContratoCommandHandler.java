package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.PedidoAquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ElaborarContratoCommandHandler implements CommandHandler<ElaborarContratoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElaborarContratoCommandHandler.class);

  private final PedidoAquisicaoViaturaService service;

  public ElaborarContratoCommandHandler(PedidoAquisicaoViaturaService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ElaborarContratoCommand command) {

    LOGGER.debug("ElaborarContratoCommand : {}", command);

    service.elaborarContrato(command.getEmprestimoId(), command.getElaboracaocontratorequest());

    return ResponseEntity.ok().build();
  }

}
