package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;

@Component
public class GetMobilidadeByIdQueryHandler implements QueryHandler<GetMobilidadeByIdQuery, ResponseEntity<MobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMobilidadeByIdQueryHandler.class);


  public GetMobilidadeByIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MobilidadeDTO> handle(GetMobilidadeByIdQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}