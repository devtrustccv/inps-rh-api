package cv.inps.rh.avaliacao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;

@Component
public class GetListaDefinicaoObjectivosQueryHandler implements QueryHandler<GetListaDefinicaoObjectivosQuery, ResponseEntity<WrapperListaAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaDefinicaoObjectivosQueryHandler.class);

  private final AvaliacaoService avaliacaoService;

  public GetListaDefinicaoObjectivosQueryHandler(AvaliacaoService avaliacaoService) {
    this.avaliacaoService = avaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaAvaliacaoDTO> handle(GetListaDefinicaoObjectivosQuery query) {

    LOGGER.debug("GetListaDefinicaoObjectivosQuery: {}", query);

    return ResponseEntity.ok(avaliacaoService.getListaDefinicaoObjectivos(query));
  }

}
