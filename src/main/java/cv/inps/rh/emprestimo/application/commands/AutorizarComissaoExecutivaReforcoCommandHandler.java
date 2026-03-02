package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.ReforcoDividaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AutorizarComissaoExecutivaReforcoCommandHandler implements CommandHandler<AutorizarComissaoExecutivaReforcoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutorizarComissaoExecutivaReforcoCommandHandler.class);

  private final ReforcoDividaService reforcoDividaService;

  public AutorizarComissaoExecutivaReforcoCommandHandler(ReforcoDividaService reforcoDividaService) {
    this.reforcoDividaService = reforcoDividaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AutorizarComissaoExecutivaReforcoCommand command) {

    LOGGER.debug("AutorizarComissaoExecutivaReforcoCommand : {}", command);

    reforcoDividaService.autorizarComissaoExecutiva(command.getEmprestimoId(), command.getAutorizacaocomissaoexecutiva());

    return ResponseEntity.ok().build();
  }

}
