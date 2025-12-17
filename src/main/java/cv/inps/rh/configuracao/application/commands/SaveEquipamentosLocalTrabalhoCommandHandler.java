package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.EquipamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveEquipamentosLocalTrabalhoCommandHandler implements CommandHandler<SaveEquipamentosLocalTrabalhoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveEquipamentosLocalTrabalhoCommandHandler.class);

  private final EquipamentoService equipamentoService;

  public SaveEquipamentosLocalTrabalhoCommandHandler(EquipamentoService equipamentoService) {
    this.equipamentoService = equipamentoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveEquipamentosLocalTrabalhoCommand command) {

    LOGGER.debug("SaveEquipamentosLocalTrabalhoCommand : {}", command);

    equipamentoService.save(command.getLocalTrabalhoId(), command.getEquipamentolistrequest());

    return ResponseEntity.ok().build();
  }

}
