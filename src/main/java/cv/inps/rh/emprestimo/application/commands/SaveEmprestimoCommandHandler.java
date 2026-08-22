package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.domain.service.process.AquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveEmprestimoCommandHandler implements CommandHandler<SaveEmprestimoCommand, ResponseEntity<IdDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveEmprestimoCommandHandler.class);

  private final AquisicaoViaturaService service;

  public SaveEmprestimoCommandHandler(AquisicaoViaturaService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<IdDTO> handle(SaveEmprestimoCommand command) {

    LOGGER.debug("SaveEmprestimoCommand : {}", command);

    var uuid = service.saveUpdatePedidoEmprestimo(command.getEmprestimoId(), command.getPedidoemprestimorequest());

    return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
  }

}
