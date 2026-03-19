package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoResponseDTO;

@Component
public class GetSubmissaoServicoProcessQueryHandler implements QueryHandler<GetSubmissaoServicoProcessQuery, ResponseEntity<MissaoSubmissaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubmissaoServicoProcessQueryHandler.class);


  public GetSubmissaoServicoProcessQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoSubmissaoResponseDTO> handle(GetSubmissaoServicoProcessQuery query) {

    LOGGER.debug("GetSubmissaoServicoProcessQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}