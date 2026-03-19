package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoResponseDTO;

@Component
public class GetMissaoServicoCabimentoQueryHandler implements QueryHandler<GetMissaoServicoCabimentoQuery, ResponseEntity<MissaoCabimentoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoCabimentoQueryHandler.class);


  public GetMissaoServicoCabimentoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoCabimentoResponseDTO> handle(GetMissaoServicoCabimentoQuery query) {

    LOGGER.debug("GetMissaoServicoCabimentoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}