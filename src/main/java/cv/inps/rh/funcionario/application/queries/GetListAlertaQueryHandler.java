package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListAlertaDTO;

@Component
public class GetListAlertaQueryHandler implements QueryHandler<GetListAlertaQuery, ResponseEntity<WrapperListAlertaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListAlertaQueryHandler.class);


  public GetListAlertaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListAlertaDTO> handle(GetListAlertaQuery query) {

    LOGGER.debug("GetListAlertaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}