package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.AlertaDTO;

@Component
public class GetAlertaQueryHandler implements QueryHandler<GetAlertaQuery, ResponseEntity<AlertaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAlertaQueryHandler.class);


  public GetAlertaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<AlertaDTO> handle(GetAlertaQuery query) {

    LOGGER.debug("GetAlertaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}