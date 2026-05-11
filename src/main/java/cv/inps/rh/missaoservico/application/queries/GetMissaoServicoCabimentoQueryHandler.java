package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMissaoServicoCabimentoQueryHandler implements
    QueryHandler<GetMissaoServicoCabimentoQuery, ResponseEntity<MissaoCabimentoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoCabimentoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetMissaoServicoCabimentoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

  @IgrpQueryHandler
  public ResponseEntity<MissaoCabimentoResponseDTO> handle(GetMissaoServicoCabimentoQuery query) {

    LOGGER.debug("GetMissaoServicoCabimentoQuery: {}", query);

    return missaoServicoServiceRead.getCabimento(query);
  }
}
