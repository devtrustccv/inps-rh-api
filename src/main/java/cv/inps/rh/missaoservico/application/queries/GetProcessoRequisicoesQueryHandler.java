package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.ProcessoRequisicoesResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessoRequisicoesQueryHandler implements QueryHandler<GetProcessoRequisicoesQuery, ResponseEntity<ProcessoRequisicoesResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessoRequisicoesQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetProcessoRequisicoesQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessoRequisicoesResponseDTO> handle(GetProcessoRequisicoesQuery query) {

    LOGGER.debug("GetProcessoRequisicoesQuery: {}", query);

    return missaoProcessoServiceRead.getRequisicoes(query);
  }

}
