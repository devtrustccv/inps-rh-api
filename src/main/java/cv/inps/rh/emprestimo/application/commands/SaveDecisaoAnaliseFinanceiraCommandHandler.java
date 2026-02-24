package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.PedidoAquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveDecisaoAnaliseFinanceiraCommandHandler implements CommandHandler<SaveDecisaoAnaliseFinanceiraCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseFinanceiraCommandHandler.class);

  private final PedidoAquisicaoViaturaService pedidoAquisicaoViaturaService;

  public SaveDecisaoAnaliseFinanceiraCommandHandler(PedidoAquisicaoViaturaService pedidoAquisicaoViaturaService) {
    this.pedidoAquisicaoViaturaService = pedidoAquisicaoViaturaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseFinanceiraCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseFinanceiraCommand : {}", command);

    pedidoAquisicaoViaturaService.saveUpdateDecisaoAnaliseFinanceira(command.getEmprestimoId(), command.getAnalisefinanceirorequest());

    return ResponseEntity.ok().build();
  }
}
