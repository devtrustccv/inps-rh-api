package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.documento.DocumentoWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveOrdemServicoCommandHandler implements CommandHandler<SaveOrdemServicoCommand, ResponseEntity<String>> {

  private final DocumentoWriteService documentoWriteService;

  public SaveOrdemServicoCommandHandler(DocumentoWriteService documentoWriteService) {
    this.documentoWriteService = documentoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveOrdemServicoCommand command) {

    documentoWriteService.saveOrdemServico(command.getFuncionarioId(), null, command.getOrdemServico());

    // TODO 27/11/2025 20:59 add return values for frontend

    return ResponseEntity.ok().build();
  }

}
