package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.AlterarEstadoContratoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AlterarEstadoContratoCommandHandler
    implements CommandHandler<AlterarEstadoContratoCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarEstadoContratoCommandHandler.class);

  private final AlterarEstadoContratoService alterarEstadoContratoService;

  public AlterarEstadoContratoCommandHandler(AlterarEstadoContratoService alterarEstadoContratoService) {
    this.alterarEstadoContratoService = alterarEstadoContratoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(AlterarEstadoContratoCommand command) {
    LOGGER.info("Alterar estado do contrato: {}", command);

    return ResponseEntity.ok(alterarEstadoContratoService.alterar(command));
  }

}
