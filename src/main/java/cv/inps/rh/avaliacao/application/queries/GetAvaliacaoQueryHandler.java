package cv.inps.rh.avaliacao.application.queries;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoReadService;

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
