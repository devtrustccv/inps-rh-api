package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;

@Component
public class GetListaFeriaQueryHandler implements QueryHandler<GetListaFeriaQuery, ResponseEntity<WrapperListaFeriaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaFeriaQueryHandler.class);


  public GetListaFeriaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaFeriaDTO> handle(GetListaFeriaQuery query) {

    LOGGER.debug("GetListaFeriaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}