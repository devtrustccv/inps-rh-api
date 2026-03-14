package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetEscalaAvaliacaoQueryHandler implements QueryHandler<GetEscalaAvaliacaoQuery, ResponseEntity<EscalaAvaliacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEscalaAvaliacaoQueryHandler.class);


  public GetEscalaAvaliacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<EscalaAvaliacaoResponseDTO> handle(GetEscalaAvaliacaoQuery query) {

    LOGGER.debug("GetEscalaAvaliacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}
