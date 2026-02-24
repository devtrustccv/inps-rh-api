package cv.inps.rh.transversal.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.transversal.application.dto.AssiduidadeListDTO;

@Component
public class RelatorioAssiduidadeQueryHandler implements QueryHandler<RelatorioAssiduidadeQuery, ResponseEntity<AssiduidadeListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(RelatorioAssiduidadeQueryHandler.class);


  public RelatorioAssiduidadeQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<AssiduidadeListDTO> handle(RelatorioAssiduidadeQuery query) {

    LOGGER.debug("RelatorioAssiduidadeQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}