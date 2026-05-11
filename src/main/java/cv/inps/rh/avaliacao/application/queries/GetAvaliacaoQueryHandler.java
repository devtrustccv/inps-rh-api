package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoResponseDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAvaliacaoQueryHandler implements QueryHandler<GetAvaliacaoQuery, ResponseEntity<AvaliacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacaoQueryHandler.class);

  private final AvaliacaoReadService avaliacaoReadService;

  public GetAvaliacaoQueryHandler(AvaliacaoReadService avaliacaoReadService) {
    this.avaliacaoReadService = avaliacaoReadService;

  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoResponseDTO> handle(GetAvaliacaoQuery query) {

    LOGGER.debug("GetAvaliacaoQuery: {}", query);

    return ResponseEntity.ok(avaliacaoReadService.getAvaliacaoFull(query.getUuid()));
  }

}
