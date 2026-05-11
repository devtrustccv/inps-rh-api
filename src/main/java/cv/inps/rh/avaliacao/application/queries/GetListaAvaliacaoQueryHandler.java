package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaAvaliacaoQueryHandler implements QueryHandler<GetListaAvaliacaoQuery, ResponseEntity<WrapperListaAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaAvaliacaoQueryHandler.class);

  private final AvaliacaoService avaliacaoService;

  public GetListaAvaliacaoQueryHandler(AvaliacaoService avaliacaoService) {
    this.avaliacaoService = avaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaAvaliacaoDTO> handle(GetListaAvaliacaoQuery query) {

    LOGGER.debug("GetListaAvaliacaoQuery: {}", query);

    return ResponseEntity.ok(avaliacaoService.getListaAvaliacao(query));
  }

}
