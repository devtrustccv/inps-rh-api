package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarNovoPagamentoCommandHandler implements CommandHandler<ValidarNovoPagamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarNovoPagamentoCommandHandler.class);

  private final RenumeracoesWriteService remuneracoesWriteService;

  public ValidarNovoPagamentoCommandHandler(RenumeracoesWriteService remuneracoesWriteService) {
    this.remuneracoesWriteService = remuneracoesWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarNovoPagamentoCommand command) {

    LOGGER.debug("Validar novo pagamento: {}", command);

    remuneracoesWriteService.validarNovoPagamento(command);

    return ResponseEntity.ok().build();
  }

}
