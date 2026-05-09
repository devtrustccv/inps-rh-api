package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMissaoServicoAutorizacaoQueryHandler implements QueryHandler<GetMissaoServicoAutorizacaoQuery, ResponseEntity<MissaoAutorizacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoAutorizacaoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetMissaoServicoAutorizacaoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoAutorizacaoResponseDTO> handle(GetMissaoServicoAutorizacaoQuery query) {

    LOGGER.debug("GetMissaoServicoAutorizacaoQuery: {}", query);

    return missaoServicoServiceRead.getAutorizacao(query);
  }

}
