package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveDecisaoAnaliseCommandHandler implements CommandHandler<SaveDecisaoAnaliseCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseCommandHandler.class);
  private final EmprestimoWriteService emprestimoWriteService;

  public SaveDecisaoAnaliseCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseCommand : {}", command);

    emprestimoWriteService.saveUpdateDecisaoAnaliseRh(command.getEmprestimoId(), command.getAnaliserhrequest());

    return ResponseEntity.ok().build();
  }

}
