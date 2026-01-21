package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.HorExtraListDTO;

@Component
public class GetListaHoraExtraQueryHandler implements QueryHandler<GetListaHoraExtraQuery, ResponseEntity<HorExtraListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaHoraExtraQueryHandler.class);


  public GetListaHoraExtraQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<HorExtraListDTO> handle(GetListaHoraExtraQuery query) {

    LOGGER.debug("GetListaHoraExtraQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}