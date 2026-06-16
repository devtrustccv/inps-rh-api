package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.VinculoSituacaoLaboralResponseDTO;
import cv.inps.rh.configuracao.application.services.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetSituacoesByVinculoQueryHandler implements QueryHandler<GetSituacoesByVinculoQuery, ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSituacoesByVinculoQueryHandler.class);

  private final ParamVinculoService paramVinculoService;

  public GetSituacoesByVinculoQueryHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>> handle(GetSituacoesByVinculoQuery query) {

    LOGGER.debug("GetSituacoesByVinculoQuery: {}", query);

    var data = paramVinculoService.listarSituacoesLaborais(query.getVinculoId());

    return ResponseEntity.ok(data);
  }

}
