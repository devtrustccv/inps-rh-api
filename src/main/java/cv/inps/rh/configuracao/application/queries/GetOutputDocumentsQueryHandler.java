package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;

@Component
public class GetOutputDocumentsQueryHandler implements QueryHandler<GetOutputDocumentsQuery, ResponseEntity<WrapperDocOutputListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetOutputDocumentsQueryHandler.class);


  public GetOutputDocumentsQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperDocOutputListDTO> handle(GetOutputDocumentsQuery query) {

    LOGGER.debug("GetOutputDocumentsQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}