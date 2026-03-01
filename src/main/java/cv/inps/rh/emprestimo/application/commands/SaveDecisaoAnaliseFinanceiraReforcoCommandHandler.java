package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.ReforcoDividaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveDecisaoAnaliseFinanceiraReforcoCommandHandler implements CommandHandler<SaveDecisaoAnaliseFinanceiraReforcoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseFinanceiraReforcoCommandHandler.class);

  private final ReforcoDividaService reforcoDividaService;

  public SaveDecisaoAnaliseFinanceiraReforcoCommandHandler(ReforcoDividaService reforcoDividaService) {
    this.reforcoDividaService = reforcoDividaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseFinanceiraReforcoCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseFinanceiraReforcoCommand : {}", command);

    reforcoDividaService.saveUpdateDecisaoAnaliseFinanceira(command.getEmprestimoId(), command.getAnalisefinanceirorequest());

    return ResponseEntity.ok().build();
  }

}
