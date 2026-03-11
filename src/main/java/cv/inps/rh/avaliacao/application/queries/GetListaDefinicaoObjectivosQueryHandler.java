package cv.inps.rh.avaliacao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;

@Component
public class GetListaDefinicaoObjectivosQueryHandler implements QueryHandler<GetListaDefinicaoObjectivosQuery, ResponseEntity<WrapperListaAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaDefinicaoObjectivosQueryHandler.class);


  public GetListaDefinicaoObjectivosQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaAvaliacaoDTO> handle(GetListaDefinicaoObjectivosQuery query) {

    LOGGER.debug("GetListaDefinicaoObjectivosQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}