package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;

@Component
public class GetDispensaQueryHandler implements QueryHandler<GetDispensaQuery, ResponseEntity<DispensaReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDispensaQueryHandler.class);


  public GetDispensaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DispensaReqDTO> handle(GetDispensaQuery query) {

    LOGGER.debug("GetDispensaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}