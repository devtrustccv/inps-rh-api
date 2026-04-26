package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class RemoverDetalheFosCommandHandler implements CommandHandler<RemoverDetalheFosCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoverDetalheFosCommandHandler.class);

  private final FosService fosService;

  public RemoverDetalheFosCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(RemoverDetalheFosCommand command) {

    LOGGER.debug("RemoverDetalheFosCommand : {}", command);

    var msg = fosService.removerDetalheFos(command.getFosDetailId());

    return ResponseEntity.ok(msg);
  }

}
