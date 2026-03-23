package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;

@Component
public class GetAnaliseProcessoMissaoServicoQueryHandler implements QueryHandler<GetAnaliseProcessoMissaoServicoQuery, ResponseEntity<MissaoAnaliseResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAnaliseProcessoMissaoServicoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetAnaliseProcessoMissaoServicoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoAnaliseResponseDTO> handle(GetAnaliseProcessoMissaoServicoQuery query) {

    LOGGER.debug("GetAnaliseProcessoMissaoServicoQuery: {}", query);

    return missaoServicoServiceRead.getAnalise(query);
  }

}
