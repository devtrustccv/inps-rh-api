package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AdiantamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveDecisaoAnaliseRhAdiantamentoCommandHandler implements CommandHandler<SaveDecisaoAnaliseRhAdiantamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseRhAdiantamentoCommandHandler.class);

  private final AdiantamentoService service;

  public SaveDecisaoAnaliseRhAdiantamentoCommandHandler(AdiantamentoService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseRhAdiantamentoCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseRhAdiantamentoCommand : {}", command);

    service.saveAnaliseRh(command.getEmprestimoId(), command.getAnaliserhadiantamentorequest());

    return ResponseEntity.ok().build();
  }

}
