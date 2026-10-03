package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class EditarSimulacaoCommandHandler implements CommandHandler<EditarSimulacaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EditarSimulacaoCommandHandler.class);

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public EditarSimulacaoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(EditarSimulacaoCommand command) {

    LOGGER.debug("EditarSimulacaoCommand : {}", command);

    progressaoPromocaoWriteService.editarSimulacao(
        command.getEditarSimulacaoDTO().getId(),
        command.getEditarSimulacaoDTO().getElegivel(),
        command.getEditarSimulacaoDTO().getValidacao(),
        command.getEditarSimulacaoDTO().getObservacao()
    );

    return ResponseEntity.ok().build();
  }

}
