package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoResponseDTO;

@Component
public class GetComponenetAvaliacaoAtualQueryHandler implements QueryHandler<GetComponenetAvaliacaoAtualQuery, ResponseEntity<ComponenteAvaliacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetComponenetAvaliacaoAtualQueryHandler.class);


  public GetComponenetAvaliacaoAtualQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<ComponenteAvaliacaoResponseDTO> handle(GetComponenetAvaliacaoAtualQuery query) {

    LOGGER.debug("GetComponenetAvaliacaoAtualQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}