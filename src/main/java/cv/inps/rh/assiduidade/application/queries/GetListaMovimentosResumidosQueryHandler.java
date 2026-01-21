package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;

@Component
public class GetListaMovimentosResumidosQueryHandler implements QueryHandler<GetListaMovimentosResumidosQuery, ResponseEntity<WrapperListaAssiduidadadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaMovimentosResumidosQueryHandler.class);


  public GetListaMovimentosResumidosQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaAssiduidadadeDTO> handle(GetListaMovimentosResumidosQuery query) {

    LOGGER.debug("GetListaMovimentosResumidosQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}