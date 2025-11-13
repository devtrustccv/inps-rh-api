package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveVinculoLaboralCommandHandler implements CommandHandler<SaveVinculoLaboralCommand, ResponseEntity<VinculoLaboralResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveVinculoLaboralCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public SaveVinculoLaboralCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<VinculoLaboralResponseDTO> handle(SaveVinculoLaboralCommand command) {

    LOGGER.info("CREATE VINCULO LABORAL REQUEST {}", command.getVinculolaboralrequest());

    var response = paramVinculoService.create(command.getVinculolaboralrequest());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
