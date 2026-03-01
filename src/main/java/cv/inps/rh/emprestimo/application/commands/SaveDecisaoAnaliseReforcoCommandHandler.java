package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.ReforcoDividaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveDecisaoAnaliseReforcoCommandHandler implements CommandHandler<SaveDecisaoAnaliseReforcoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseReforcoCommandHandler.class);

  private final ReforcoDividaService reforcoDividaService;

  public SaveDecisaoAnaliseReforcoCommandHandler(ReforcoDividaService reforcoDividaService) {

    this.reforcoDividaService = reforcoDividaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseReforcoCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseReforcoCommand : {}", command);

    reforcoDividaService.saveUpdateDecisaoAnaliseRh(command.getEmprestimoId(), command.getAnaliserhrequest());

    return ResponseEntity.ok().build();
  }

}
