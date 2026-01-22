package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.DetalheMapaFeriaDTO;

@Component
public class GetDetalheMapaFeriaQueryHandler implements QueryHandler<GetDetalheMapaFeriaQuery, ResponseEntity<DetalheMapaFeriaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheMapaFeriaQueryHandler.class);


  public GetDetalheMapaFeriaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DetalheMapaFeriaDTO> handle(GetDetalheMapaFeriaQuery query) {

    LOGGER.debug("GetDetalheMapaFeriaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}