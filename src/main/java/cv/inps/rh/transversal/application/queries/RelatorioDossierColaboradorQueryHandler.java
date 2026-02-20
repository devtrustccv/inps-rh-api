package cv.inps.rh.transversal.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.transversal.application.dto.DossierColaboradorListDTO;

@Component
public class RelatorioDossierColaboradorQueryHandler implements QueryHandler<RelatorioDossierColaboradorQuery, ResponseEntity<DossierColaboradorListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(RelatorioDossierColaboradorQueryHandler.class);


  public RelatorioDossierColaboradorQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DossierColaboradorListDTO> handle(RelatorioDossierColaboradorQuery query) {

    LOGGER.debug("RelatorioDossierColaboradorQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}