package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.domain.service.process.ReforcoDividaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SavePedidosReforcoCommandHandler implements CommandHandler<SavePedidosReforcoCommand, ResponseEntity<IdDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SavePedidosReforcoCommandHandler.class);

  private final ReforcoDividaService reforcoDividaService;

  public SavePedidosReforcoCommandHandler(ReforcoDividaService reforcoDividaService) {
    this.reforcoDividaService = reforcoDividaService;
  }

  @IgrpCommandHandler
  public ResponseEntity<IdDTO> handle(SavePedidosReforcoCommand command) {

    LOGGER.debug("SavePedidosReforcoCommand : {}", command);

    var id = reforcoDividaService.saveUpdatePedidoReforco(command.getPedidoreforcorequest());

    return ResponseEntity.ok(new IdDTO(id));
  }
}
