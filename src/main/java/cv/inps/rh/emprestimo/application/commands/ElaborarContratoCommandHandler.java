package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ElaborarContratoCommandHandler implements CommandHandler<ElaborarContratoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElaborarContratoCommandHandler.class);

  private final AquisicaoViaturaService pedidoAquisicaoViaturaService;

  public ElaborarContratoCommandHandler(AquisicaoViaturaService pedidoAquisicaoViaturaService) {
    this.pedidoAquisicaoViaturaService = pedidoAquisicaoViaturaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ElaborarContratoCommand command) {

    LOGGER.debug("ElaborarContratoCommand : {}", command);

    pedidoAquisicaoViaturaService.elaborarContrato(command.getEmprestimoId(), command.getElaboracaocontratorequest());

    return ResponseEntity.ok().build();
  }
}
