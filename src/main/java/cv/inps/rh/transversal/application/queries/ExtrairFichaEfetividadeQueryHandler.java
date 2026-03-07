package cv.inps.rh.transversal.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class ExtrairFichaEfetividadeQueryHandler implements QueryHandler<ExtrairFichaEfetividadeQuery, ResponseEntity<?>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtrairFichaEfetividadeQueryHandler.class);


  public ExtrairFichaEfetividadeQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<?> handle(ExtrairFichaEfetividadeQuery query) {

    LOGGER.debug("ExtrairFichaEfetividadeQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}