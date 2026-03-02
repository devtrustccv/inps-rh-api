package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AnexarOrdemServicoCommandHandler implements CommandHandler<AnexarOrdemServicoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnexarOrdemServicoCommandHandler.class);

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public AnexarOrdemServicoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AnexarOrdemServicoCommand command) {

    LOGGER.debug("AnexarOrdemServicoCommand : {}", command);

    progressaoPromocaoWriteService.anexarOrdemServico(command.getAnexarordemservicorequest());

    return ResponseEntity.ok().build();
  }

}
