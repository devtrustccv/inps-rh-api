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
public class GetComponenteAvaliacaoQueryHandler implements QueryHandler<GetComponenteAvaliacaoQuery, ResponseEntity<ComponenteAvaliacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetComponenteAvaliacaoQueryHandler.class);


  public GetComponenteAvaliacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<ComponenteAvaliacaoResponseDTO> handle(GetComponenteAvaliacaoQuery query) {

    LOGGER.debug("GetComponenteAvaliacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}