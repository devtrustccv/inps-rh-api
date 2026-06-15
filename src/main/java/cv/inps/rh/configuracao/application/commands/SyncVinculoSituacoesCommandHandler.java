package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.VinculoSituacaoLaboralResponseDTO;
import cv.inps.rh.configuracao.application.services.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SyncVinculoSituacoesCommandHandler implements CommandHandler<SyncVinculoSituacoesCommand, ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SyncVinculoSituacoesCommandHandler.class);

  private final ParamVinculoService paramVinculoService;

  public SyncVinculoSituacoesCommandHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpCommandHandler
  public ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>> handle(SyncVinculoSituacoesCommand command) {

    LOGGER.debug("SyncVinculoSituacoesCommand: {}", command);

    var data = paramVinculoService.syncSituacoesLaborais(command.getVinculoUuid(), command.getSituacoes());

    return ResponseEntity.ok(data);
  }

}
