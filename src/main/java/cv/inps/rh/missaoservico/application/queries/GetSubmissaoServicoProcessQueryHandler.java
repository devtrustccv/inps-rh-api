package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;

@Component
public class GetSubmissaoServicoProcessQueryHandler implements QueryHandler<GetSubmissaoServicoProcessQuery, ResponseEntity<MissaoSubmissaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubmissaoServicoProcessQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetSubmissaoServicoProcessQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoSubmissaoResponseDTO> handle(GetSubmissaoServicoProcessQuery query) {

    LOGGER.debug("GetSubmissaoServicoProcessQuery: {}", query);
    return missaoServicoServiceRead.getSubmissao(query);
  }

}
