package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoColaboradorCommandHandler implements CommandHandler<ValidacaoColaboradorCommand, ResponseEntity<AtivarInativarColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoColaboradorCommandHandler.class);


  public ValidacaoColaboradorCommandHandler() {

  }

  @IgrpCommandHandler
  public ResponseEntity<AtivarInativarColaboradorDTO> handle(ValidacaoColaboradorCommand command) {
     return null;
  }

}
