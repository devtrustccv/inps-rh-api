package cv.inps.rh.avaliacao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoFinalDTO;

@Component
public class GetAvaliacaoFinalQueryHandler implements QueryHandler<GetAvaliacaoFinalQuery, ResponseEntity<AvaliacaoFinalDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacaoFinalQueryHandler.class);


  public GetAvaliacaoFinalQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoFinalDTO> handle(GetAvaliacaoFinalQuery query) {

    LOGGER.debug("GetAvaliacaoFinalQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}