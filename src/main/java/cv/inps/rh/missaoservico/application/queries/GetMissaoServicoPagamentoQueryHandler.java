package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoPagamentoResponseDTO;

@Component
public class GetMissaoServicoPagamentoQueryHandler implements QueryHandler<GetMissaoServicoPagamentoQuery, ResponseEntity<MissaoPagamentoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoPagamentoQueryHandler.class);


  public GetMissaoServicoPagamentoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoPagamentoResponseDTO> handle(GetMissaoServicoPagamentoQuery query) {

    LOGGER.debug("GetMissaoServicoPagamentoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}