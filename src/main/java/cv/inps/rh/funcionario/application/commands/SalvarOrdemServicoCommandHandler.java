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
public class SalvarOrdemServicoCommandHandler implements CommandHandler<SalvarOrdemServicoCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SalvarOrdemServicoCommandHandler.class);

  private final OrdemServicoReadService ordemServicoReadService;

  public SalvarOrdemServicoCommandHandler(OrdemServicoReadService ordemServicoReadService) {
    this.ordemServicoReadService = ordemServicoReadService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(SalvarOrdemServicoCommand command) {
    LOGGER.debug("SalvarOrdemServicoCommand: {}", command);
    ordemServicoReadService.salvar(command.getFuncionarioUuid(), command.getItems());
    return ResponseEntity.ok(Map.of("message", "Ordens de serviço gravadas com sucesso"));
  }

}
