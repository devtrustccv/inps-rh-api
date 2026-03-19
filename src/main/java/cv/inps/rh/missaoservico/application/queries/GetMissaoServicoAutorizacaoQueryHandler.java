package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoResponseDTO;

@Component
public class GetMissaoServicoAutorizacaoQueryHandler implements QueryHandler<GetMissaoServicoAutorizacaoQuery, ResponseEntity<MissaoAutorizacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoAutorizacaoQueryHandler.class);


  public GetMissaoServicoAutorizacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoAutorizacaoResponseDTO> handle(GetMissaoServicoAutorizacaoQuery query) {

    LOGGER.debug("GetMissaoServicoAutorizacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}