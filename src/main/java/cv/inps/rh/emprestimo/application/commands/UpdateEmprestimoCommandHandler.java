package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class UpdateEmprestimoCommandHandler implements CommandHandler<UpdateEmprestimoCommand, ResponseEntity<IdDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEmprestimoCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public UpdateEmprestimoCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<IdDTO> handle(UpdateEmprestimoCommand command) {

    LOGGER.debug("UpdateEmprestimoCommand : {}", command);

    var uuid = emprestimoWriteService.saveUpdatePedidoEmprestimo(command.getEmprestimoId(), command.getPedidoemprestimo());

    return ResponseEntity.ok(uuid);
  }

}
