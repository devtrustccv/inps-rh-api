package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.AlterarSituacaoLaboralWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AlterarSituacaoLaboralCommandHandler implements CommandHandler<AlterarSituacaoLaboralCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarSituacaoLaboralCommandHandler.class);

  private final AlterarSituacaoLaboralWriteService alterarSituacaoLaboralWriteService;

  public AlterarSituacaoLaboralCommandHandler(AlterarSituacaoLaboralWriteService alterarSituacaoLaboralWriteService) {
    this.alterarSituacaoLaboralWriteService = alterarSituacaoLaboralWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(AlterarSituacaoLaboralCommand command) {
    LOGGER.info("Alterando situacao laboral do funcionario: {}", command.getId());
    return ResponseEntity.ok(alterarSituacaoLaboralWriteService.execute(command));
  }

}
