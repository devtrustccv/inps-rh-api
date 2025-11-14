package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.TipoContratoLaboralService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class DeleteTipoContratoLaboralCommandHandler implements CommandHandler<DeleteTipoContratoLaboralCommand, ResponseEntity<String>> {

  private final TipoContratoLaboralService tipoContratoLaboralService;

  public DeleteTipoContratoLaboralCommandHandler(TipoContratoLaboralService tipoContratoLaboralService) {
    this.tipoContratoLaboralService = tipoContratoLaboralService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(DeleteTipoContratoLaboralCommand command) {

    tipoContratoLaboralService.delete(command.getTipoContratoLaboralId());

    return ResponseEntity.noContent().build();
  }

}
