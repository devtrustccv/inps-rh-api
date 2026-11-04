package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class AlterarEstadoSubsidioNatalCommandHandler implements CommandHandler<AlterarEstadoSubsidioNatalCommand, ResponseEntity<Map<String, Object>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarEstadoSubsidioNatalCommandHandler.class);

  private final SubsidioService subsidioService;

  public AlterarEstadoSubsidioNatalCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, Object>> handle(AlterarEstadoSubsidioNatalCommand command) {

    LOGGER.debug("AlterarEstadoSubsidioNatalCommand : {}", command);

    var total = subsidioService.alterarEstadoSubsidioNatal(command.getRequest().getSubsidioIds());
    return ResponseEntity.ok(Map.of("atualizados", total));
  }

}
