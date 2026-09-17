package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.AvaliacaoPrestadorResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAvaliacaoPrestadorQueryHandler implements QueryHandler<GetAvaliacaoPrestadorQuery, ResponseEntity<AvaliacaoPrestadorResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacaoPrestadorQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetAvaliacaoPrestadorQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoPrestadorResponseDTO> handle(GetAvaliacaoPrestadorQuery query) {

    LOGGER.debug("GetAvaliacaoPrestadorQuery: {}", query);

    return missaoProcessoServiceRead.getAvaliacao(query);
  }

}
