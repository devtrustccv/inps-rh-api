package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AdiantamentoEmprestimoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AnexarComprovativoPagamentoCommandHandler implements CommandHandler<AnexarComprovativoPagamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnexarComprovativoPagamentoCommandHandler.class);

  private final AdiantamentoEmprestimoService service;

  public AnexarComprovativoPagamentoCommandHandler(AdiantamentoEmprestimoService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AnexarComprovativoPagamentoCommand command) {

    LOGGER.debug("AnexarComprovativoPagamentoCommand : {}", command);

    service.anexarComprovativo(command.getEmprestimoId(), command.getDocumento());

    return ResponseEntity.ok().build();
  }

}
