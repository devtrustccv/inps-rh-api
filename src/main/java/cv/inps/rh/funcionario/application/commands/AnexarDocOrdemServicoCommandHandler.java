package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.documento.OrdemServicoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AnexarDocOrdemServicoCommandHandler implements CommandHandler<AnexarDocOrdemServicoCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnexarDocOrdemServicoCommandHandler.class);

  private final OrdemServicoReadService ordemServicoReadService;

  public AnexarDocOrdemServicoCommandHandler(OrdemServicoReadService ordemServicoReadService) {
    this.ordemServicoReadService = ordemServicoReadService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(AnexarDocOrdemServicoCommand command) {
    LOGGER.debug("AnexarDocOrdemServicoCommand: {}", command);
    ordemServicoReadService.anexar(command.getOsUuid(), command.getAnexo());
    return ResponseEntity.ok(Map.of("message", "Documento anexado com sucesso"));
  }

}
