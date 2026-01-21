package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;

@Component
public class GetListaFaltaQueryHandler implements QueryHandler<GetListaFaltaQuery, ResponseEntity<WrapperListaFaltaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaFaltaQueryHandler.class);


  public GetListaFaltaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaFaltaDTO> handle(GetListaFaltaQuery query) {

    LOGGER.debug("GetListaFaltaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}