package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.DocOutputResponseDTO;

@Component
public class GetOutputDocumentByIdQueryHandler implements QueryHandler<GetOutputDocumentByIdQuery, ResponseEntity<DocOutputResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetOutputDocumentByIdQueryHandler.class);


  public GetOutputDocumentByIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DocOutputResponseDTO> handle(GetOutputDocumentByIdQuery query) {

    LOGGER.debug("GetOutputDocumentByIdQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}