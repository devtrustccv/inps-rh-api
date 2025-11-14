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
public class SaveTipoContratoLaboralCommandHandler implements CommandHandler<SaveTipoContratoLaboralCommand, ResponseEntity<TipoContratoLaboralRequestDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveTipoContratoLaboralCommandHandler.class);

  private final TipoContratoLaboralService tipoContratoLaboralService;

  public SaveTipoContratoLaboralCommandHandler(TipoContratoLaboralService tipoContratoLaboralService) {
    this.tipoContratoLaboralService = tipoContratoLaboralService;
  }

  @IgrpCommandHandler
  public ResponseEntity<TipoContratoLaboralRequestDTO> handle(SaveTipoContratoLaboralCommand command) {

    LOGGER.info("CREATE TIPO CONTRATO LABORAL REQUEST {}", command.getTipocontratolaboralrequest());

    var response = tipoContratoLaboralService.create(command.getTipocontratolaboralrequest());

    return ResponseEntity.status(201).body(response);
  }

}
