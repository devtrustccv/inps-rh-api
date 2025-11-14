package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import cv.inps.rh.configuracao.domain.service.TipoContratoLaboralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateTipoContratoLaboralCommandHandler implements CommandHandler<UpdateTipoContratoLaboralCommand, ResponseEntity<TipoContratoLaboralRequestDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTipoContratoLaboralCommandHandler.class);

  private final TipoContratoLaboralService tipoContratoLaboralService;

  public UpdateTipoContratoLaboralCommandHandler(TipoContratoLaboralService tipoContratoLaboralService) {
    this.tipoContratoLaboralService = tipoContratoLaboralService;
  }

  @IgrpCommandHandler
  public ResponseEntity<TipoContratoLaboralRequestDTO> handle(UpdateTipoContratoLaboralCommand command) {

    LOGGER.info("UPDATE TIPO CONTRATO LABORAL REQUEST {}", command);

    var response = tipoContratoLaboralService.update(command.getTipoContratoLaboralId(), command.getTipocontratolaboralrequest());

    return ResponseEntity.ok().body(response);
  }

}
