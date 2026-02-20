package cv.inps.rh.transversal.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class DownloadRelatorioQueryHandler implements QueryHandler<DownloadRelatorioQuery, ResponseEntity<?>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadRelatorioQueryHandler.class);


  public DownloadRelatorioQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<?> handle(DownloadRelatorioQuery query) {

    LOGGER.debug("DownloadRelatorioQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}