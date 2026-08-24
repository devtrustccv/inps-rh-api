package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.process.AquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveDecisaoAnaliseCommandHandler implements CommandHandler<SaveDecisaoAnaliseCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseCommandHandler.class);

  private final AquisicaoViaturaService pedidoAquisicaoViaturaService;

  public SaveDecisaoAnaliseCommandHandler(AquisicaoViaturaService pedidoAquisicaoViaturaService) {
    this.pedidoAquisicaoViaturaService = pedidoAquisicaoViaturaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseCommand command) {

    LOGGER.debug("SaveDecisaoAnaliseCommand : {}", command);

    pedidoAquisicaoViaturaService.saveUpdateDecisaoAnaliseRh(command.getEmprestimoId(), command.getAnaliserhrequest());

    return ResponseEntity.ok().build();
  }

}
