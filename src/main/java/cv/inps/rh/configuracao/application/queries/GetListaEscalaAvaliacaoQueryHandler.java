package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.WrapperListaEscalaAvaliacaoDTO;

@Component
public class GetListaEscalaAvaliacaoQueryHandler implements QueryHandler<GetListaEscalaAvaliacaoQuery, ResponseEntity<WrapperListaEscalaAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaEscalaAvaliacaoQueryHandler.class);


  public GetListaEscalaAvaliacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaEscalaAvaliacaoDTO> handle(GetListaEscalaAvaliacaoQuery query) {

    LOGGER.debug("GetListaEscalaAvaliacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}