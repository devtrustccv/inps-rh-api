package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;

@Component
public class GetListaDispensaQueryHandler implements QueryHandler<GetListaDispensaQuery, ResponseEntity<WrapperListaDispensaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaDispensaQueryHandler.class);


  public GetListaDispensaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaDispensaDTO> handle(GetListaDispensaQuery query) {

    LOGGER.debug("GetListaDispensaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}