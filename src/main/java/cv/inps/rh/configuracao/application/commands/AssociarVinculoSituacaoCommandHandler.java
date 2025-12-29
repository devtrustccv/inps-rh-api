package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AssociarVinculoSituacaoCommandHandler implements CommandHandler<AssociarVinculoSituacaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssociarVinculoSituacaoCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public AssociarVinculoSituacaoCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AssociarVinculoSituacaoCommand command) {

    LOGGER.debug("AssociarVinculoSituacaoCommand : {}", command);

    paramVinculoService.associarVinculoSituacao(command.getVinculoId(), command.getSituacaoId());

    return ResponseEntity.ok().build();
  }

}
