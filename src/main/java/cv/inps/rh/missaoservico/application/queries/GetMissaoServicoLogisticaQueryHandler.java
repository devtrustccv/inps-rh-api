package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMissaoServicoLogisticaQueryHandler
    implements QueryHandler<GetMissaoServicoLogisticaQuery, ResponseEntity<MissaoLogisticaResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoLogisticaQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetMissaoServicoLogisticaQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;

  }

  @IgrpQueryHandler
  public ResponseEntity<MissaoLogisticaResponseDTO> handle(GetMissaoServicoLogisticaQuery query) {

    LOGGER.debug("GetMissaoServicoLogisticaQuery: {}", query);
    return missaoServicoServiceRead.getLogistica(query);
  }
}
