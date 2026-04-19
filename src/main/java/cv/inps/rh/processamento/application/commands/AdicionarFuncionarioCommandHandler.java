package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AdicionarFuncionarioCommandHandler implements CommandHandler<AdicionarFuncionarioCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdicionarFuncionarioCommandHandler.class);

  private final FosService fosService;

  public AdicionarFuncionarioCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AdicionarFuncionarioCommand command) {

    LOGGER.debug("AdicionarFuncionarioCommand : {}", command);

    fosService.adicionarFuncionario(command);

    return ResponseEntity.ok().build();
  }

}
