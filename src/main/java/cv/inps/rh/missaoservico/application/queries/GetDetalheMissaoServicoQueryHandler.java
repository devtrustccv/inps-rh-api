package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoServicoResponseDTO;

@Component
public class GetDetalheMissaoServicoQueryHandler implements QueryHandler<GetDetalheMissaoServicoQuery, ResponseEntity<MissaoServicoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheMissaoServicoQueryHandler.class);


  public GetDetalheMissaoServicoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoServicoResponseDTO> handle(GetDetalheMissaoServicoQuery query) {

    LOGGER.debug("GetDetalheMissaoServicoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}