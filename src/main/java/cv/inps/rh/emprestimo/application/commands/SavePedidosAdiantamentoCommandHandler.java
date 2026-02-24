package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.domain.service.process.AdiantamentoEmprestimoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SavePedidosAdiantamentoCommandHandler implements CommandHandler<SavePedidosAdiantamentoCommand, ResponseEntity<IdDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SavePedidosAdiantamentoCommandHandler.class);

  private final AdiantamentoEmprestimoService service;

  public SavePedidosAdiantamentoCommandHandler(AdiantamentoEmprestimoService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<IdDTO> handle(SavePedidosAdiantamentoCommand command) {

    LOGGER.debug("SavePedidosAdiantamentoCommand : {}", command);

    var uuid = service.saveUpdatePedidoAdiantamento(command.getPedidoadiantamentorequest());

    return ResponseEntity.ok(new IdDTO(uuid));
  }

}
