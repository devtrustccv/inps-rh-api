package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoServicoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalheMissaoServicoQueryHandler implements QueryHandler<GetDetalheMissaoServicoQuery, ResponseEntity<MissaoServicoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheMissaoServicoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetDetalheMissaoServicoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoServicoResponseDTO> handle(GetDetalheMissaoServicoQuery query) {

    LOGGER.debug("GetDetalheMissaoServicoQuery: {}", query);

    return missaoServicoServiceRead.getDetalhe(query);
  }

}
