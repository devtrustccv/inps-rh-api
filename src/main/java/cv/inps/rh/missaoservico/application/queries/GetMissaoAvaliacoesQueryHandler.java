package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoAvaliacoesResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMissaoAvaliacoesQueryHandler implements QueryHandler<GetMissaoAvaliacoesQuery, ResponseEntity<MissaoAvaliacoesResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoAvaliacoesQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetMissaoAvaliacoesQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoAvaliacoesResponseDTO> handle(GetMissaoAvaliacoesQuery query) {

    LOGGER.debug("GetMissaoAvaliacoesQuery: {}", query);

    return missaoProcessoServiceRead.getAvaliacoesDaMissao(query);
  }

}
