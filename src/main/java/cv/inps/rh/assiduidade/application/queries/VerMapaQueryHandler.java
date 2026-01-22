package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.VerMapaDTO;

@Component
public class VerMapaQueryHandler implements QueryHandler<VerMapaQuery, ResponseEntity<VerMapaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(VerMapaQueryHandler.class);


  public VerMapaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<VerMapaDTO> handle(VerMapaQuery query) {

    LOGGER.debug("VerMapaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}