package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;

@Component
public class GetListaPicagemQueryHandler implements QueryHandler<GetListaPicagemQuery, ResponseEntity<WrapperListaPicagemDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaPicagemQueryHandler.class);


  public GetListaPicagemQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaPicagemDTO> handle(GetListaPicagemQuery query) {

    LOGGER.debug("GetListaPicagemQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}