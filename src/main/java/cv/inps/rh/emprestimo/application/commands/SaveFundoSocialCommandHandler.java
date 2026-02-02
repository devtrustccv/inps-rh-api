package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveFundoSocialCommandHandler implements CommandHandler<SaveFundoSocialCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveFundoSocialCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public SaveFundoSocialCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveFundoSocialCommand command) {

    LOGGER.debug("SaveFundoSocialCommand : {}", command);

    emprestimoWriteService.saveFundoSocial(command.getFundosocialrequest());

    return ResponseEntity.ok().build();
  }
}
