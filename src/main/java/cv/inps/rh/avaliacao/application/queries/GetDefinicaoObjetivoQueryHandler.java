package cv.inps.rh.avaliacao.application.queries;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoReadService;

@Component
public class GetDefinicaoObjetivoQueryHandler implements QueryHandler<GetDefinicaoObjetivoQuery, ResponseEntity<AvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDefinicaoObjetivoQueryHandler.class);

  private final AvaliacaoReadService avaliacaoReadService;

  public GetDefinicaoObjetivoQueryHandler(AvaliacaoReadService avaliacaoReadService) {
    this.avaliacaoReadService = avaliacaoReadService;

  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoDTO> handle(GetDefinicaoObjetivoQuery query) {

    LOGGER.debug("GetDefinicaoObjetivoQuery: {}", query);

    return ResponseEntity.ok(avaliacaoReadService.getDefinicaoObjetivo(query.getUuid()));
  }

}
