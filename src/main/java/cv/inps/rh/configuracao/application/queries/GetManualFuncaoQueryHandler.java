package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.ManualFuncaoResponseDTO;

@Component
public class GetManualFuncaoQueryHandler implements QueryHandler<GetManualFuncaoQuery, ResponseEntity<ManualFuncaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetManualFuncaoQueryHandler.class);


  public GetManualFuncaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<ManualFuncaoResponseDTO> handle(GetManualFuncaoQuery query) {

    LOGGER.debug("GetManualFuncaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}