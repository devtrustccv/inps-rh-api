package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateVinculoLaboralCommandHandler implements CommandHandler<UpdateVinculoLaboralCommand, ResponseEntity<VinculoLaboralResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateVinculoLaboralCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public UpdateVinculoLaboralCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<VinculoLaboralResponseDTO> handle(UpdateVinculoLaboralCommand command) {

    LOGGER.info("UPDATE VINCULO LABORAL REQUEST {}", command);

    var response = paramVinculoService.update(command.getVinculoLaboralId(), command.getVinculolaboralrequest());

    return ResponseEntity.ok().body(response);
  }

}
