package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveConfiguracaoInfoEmprestimoCommandHandler implements CommandHandler<SaveConfiguracaoInfoEmprestimoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveConfiguracaoInfoEmprestimoCommandHandler.class);
  private final EmprestimoWriteService emprestimoWriteService;

  public SaveConfiguracaoInfoEmprestimoCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveConfiguracaoInfoEmprestimoCommand command) {

    LOGGER.debug("SaveConfiguracaoInfoEmprestimoCommand : {}", command);

    emprestimoWriteService.saveConfiguracaoEmprestimo(command);

    return ResponseEntity.ok().build();
  }

}
