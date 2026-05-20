package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.AlterarSituacaoLaboralRequest;
import cv.inps.rh.funcionario.application.service.AlterarSituacaoLaboralWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AlterarSituacaoLaboralCommandHandler implements CommandHandler<AlterarSituacaoLaboralCommand, ResponseEntity<AlterarSituacaoLaboralRequest>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarSituacaoLaboralCommandHandler.class);

  private final AlterarSituacaoLaboralWriteService alterarSituacaoLaboralWriteService;

  public AlterarSituacaoLaboralCommandHandler(AlterarSituacaoLaboralWriteService alterarSituacaoLaboralWriteService) {
    this.alterarSituacaoLaboralWriteService = alterarSituacaoLaboralWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<AlterarSituacaoLaboralRequest> handle(AlterarSituacaoLaboralCommand command) {
    LOGGER.info("Alterando situacao laboral do funcionario: {}", command.getId());
    return ResponseEntity.ok(alterarSituacaoLaboralWriteService.execute(command));
  }

}
