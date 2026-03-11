package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.WrapperListaManualFuncaoDTO;

@Component
public class GetListaManualFuncaoQueryHandler implements QueryHandler<GetListaManualFuncaoQuery, ResponseEntity<WrapperListaManualFuncaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaManualFuncaoQueryHandler.class);


  public GetListaManualFuncaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaManualFuncaoDTO> handle(GetListaManualFuncaoQuery query) {

    LOGGER.debug("GetListaManualFuncaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}