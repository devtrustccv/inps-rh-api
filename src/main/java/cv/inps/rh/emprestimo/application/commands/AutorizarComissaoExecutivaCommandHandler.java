package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AutorizarComissaoExecutivaCommandHandler implements CommandHandler<AutorizarComissaoExecutivaCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutorizarComissaoExecutivaCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public AutorizarComissaoExecutivaCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AutorizarComissaoExecutivaCommand command) {

    LOGGER.debug("AutorizarComissaoExecutivaCommand : {}", command);

    emprestimoWriteService.autorizarComissaoExecutiva(command.getEmprestimoId(), command.getAutorizacaocomissaoexecutiva());

    return ResponseEntity.ok().build();
  }

}
