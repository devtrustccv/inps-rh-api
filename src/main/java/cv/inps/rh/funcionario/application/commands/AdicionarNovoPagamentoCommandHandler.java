package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AdicionarNovoPagamentoCommandHandler implements CommandHandler<AdicionarNovoPagamentoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdicionarNovoPagamentoCommandHandler.class);

  private final RenumeracoesWriteService renumeracoesWriteService;

  public AdicionarNovoPagamentoCommandHandler(RenumeracoesWriteService renumeracoesWriteService) {
    this.renumeracoesWriteService = renumeracoesWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AdicionarNovoPagamentoCommand command) {

    LOGGER.info("Handling novo pagamento command with request: {}", command);

    renumeracoesWriteService.novoPagamento(command.getFuncionarioId(), command.getNovopagamentorequest());

    return ResponseEntity.ok().build();
  }

}
