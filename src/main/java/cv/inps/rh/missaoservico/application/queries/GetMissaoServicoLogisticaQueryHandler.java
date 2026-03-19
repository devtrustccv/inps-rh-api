package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaResponseDTO;

@Component
public class GetMissaoServicoLogisticaQueryHandler implements QueryHandler<GetMissaoServicoLogisticaQuery, ResponseEntity<MissaoLogisticaResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoLogisticaQueryHandler.class);


  public GetMissaoServicoLogisticaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoLogisticaResponseDTO> handle(GetMissaoServicoLogisticaQuery query) {

    LOGGER.debug("GetMissaoServicoLogisticaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}