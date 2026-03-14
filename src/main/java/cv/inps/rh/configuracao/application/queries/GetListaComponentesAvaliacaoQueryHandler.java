package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.WrapperListComponenteAvaliacaoDTO;

@Component
public class GetListaComponentesAvaliacaoQueryHandler implements QueryHandler<GetListaComponentesAvaliacaoQuery, ResponseEntity<WrapperListComponenteAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaComponentesAvaliacaoQueryHandler.class);


  public GetListaComponentesAvaliacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListComponenteAvaliacaoDTO> handle(GetListaComponentesAvaliacaoQuery query) {

    LOGGER.debug("GetListaComponentesAvaliacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}