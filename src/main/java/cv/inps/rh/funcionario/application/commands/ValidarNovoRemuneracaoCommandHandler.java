package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarNovoRemuneracaoCommandHandler implements CommandHandler<ValidarNovoRemuneracaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarNovoRemuneracaoCommandHandler.class);

  private final RenumeracoesWriteService remuneracoesWriteService;

  public ValidarNovoRemuneracaoCommandHandler(RenumeracoesWriteService remuneracoesWriteService) {
    this.remuneracoesWriteService = remuneracoesWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarNovoRemuneracaoCommand command) {

    LOGGER.debug("Validar novo remuneracao: {}", command);

    remuneracoesWriteService.validarNovoRemuneracao(command);

    return ResponseEntity.ok().build();
  }

}
