package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseResponseDTO;

@Component
public class GetAnaliseProcessoMissaoServicoQueryHandler implements QueryHandler<GetAnaliseProcessoMissaoServicoQuery, ResponseEntity<MissaoAnaliseResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAnaliseProcessoMissaoServicoQueryHandler.class);


  public GetAnaliseProcessoMissaoServicoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoAnaliseResponseDTO> handle(GetAnaliseProcessoMissaoServicoQuery query) {

    LOGGER.debug("GetAnaliseProcessoMissaoServicoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}