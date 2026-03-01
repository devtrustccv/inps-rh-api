package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.ReforcoDividaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ElaborarContratoReforcoCommandHandler implements CommandHandler<ElaborarContratoReforcoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElaborarContratoReforcoCommandHandler.class);

  private final ReforcoDividaService reforcoDividaService;

  public ElaborarContratoReforcoCommandHandler(ReforcoDividaService reforcoDividaService) {
    this.reforcoDividaService = reforcoDividaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ElaborarContratoReforcoCommand command) {

    LOGGER.debug("ElaborarContratoReforcoCommand : {}", command);

    reforcoDividaService.elaborarContrato(command.getEmprestimoId(), command.getElaboracaocontratorequest());

    return ResponseEntity.ok().build();
  }

}
