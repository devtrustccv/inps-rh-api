package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.service.NovoContratoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class NovoContratoCommandHandler implements CommandHandler<NovoContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoContratoCommandHandler.class);

  private final NovoContratoService novoContratoService;

  public NovoContratoCommandHandler(NovoContratoService novoContratoService) {
    this.novoContratoService = novoContratoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(NovoContratoCommand command) {
     LOGGER.info("Novo contrato para funcionario: {}", command);

     return ResponseEntity.ok(novoContratoService.registrar(command));
  }

}
