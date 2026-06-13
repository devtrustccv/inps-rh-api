package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.service.VinculoMovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetMovimentosByVinculoQueryHandler implements QueryHandler<GetMovimentosByVinculoQuery, ResponseEntity<List<VinculoMovimentoResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMovimentosByVinculoQueryHandler.class);

  private final VinculoMovimentoService vinculoMovimentoService;

  public GetMovimentosByVinculoQueryHandler(VinculoMovimentoService vinculoMovimentoService) {
    this.vinculoMovimentoService = vinculoMovimentoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<VinculoMovimentoResponseDTO>> handle(GetMovimentosByVinculoQuery query) {

    LOGGER.debug("GetMovimentosByVinculoQuery: {}", query);

    var data = vinculoMovimentoService.listarPorVinculo(query.getVinculoId());

    return ResponseEntity.ok(data);
  }

}
