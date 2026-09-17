package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.ProcessoCabimentoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessoCabimentoQueryHandler implements QueryHandler<GetProcessoCabimentoQuery, ResponseEntity<ProcessoCabimentoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessoCabimentoQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetProcessoCabimentoQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessoCabimentoResponseDTO> handle(GetProcessoCabimentoQuery query) {

    LOGGER.debug("GetProcessoCabimentoQuery: {}", query);

    return missaoProcessoServiceRead.getCabimento(query);
  }

}
