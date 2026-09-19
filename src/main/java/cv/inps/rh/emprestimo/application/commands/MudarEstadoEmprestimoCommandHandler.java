package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class MudarEstadoEmprestimoCommandHandler implements CommandHandler<MudarEstadoEmprestimoCommand, ResponseEntity<Void>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MudarEstadoEmprestimoCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public MudarEstadoEmprestimoCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Void> handle(MudarEstadoEmprestimoCommand command) {

    LOGGER.debug("AnexarComprovativoPagamentoCommand : {}", command);

    var data = command.getData();

    emprestimoWriteService.mudarEstadoEmprestimo(
        command.getEmprestimoId(),
        data.estado(),
        data.observacao(),
        data.files()
    );

    return ResponseEntity.ok().build();
  }

}
