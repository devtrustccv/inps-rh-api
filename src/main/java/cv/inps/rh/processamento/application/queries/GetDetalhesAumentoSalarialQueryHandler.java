package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.AumentoSalarialResponseDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalhesAumentoSalarialQueryHandler implements QueryHandler<GetDetalhesAumentoSalarialQuery, ResponseEntity<AumentoSalarialResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalhesAumentoSalarialQueryHandler.class);

  private final AumentoSalarialReadService aumentoSalarialService;

  public GetDetalhesAumentoSalarialQueryHandler(AumentoSalarialReadService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<AumentoSalarialResponseDTO> handle(GetDetalhesAumentoSalarialQuery query) {

    LOGGER.debug("GetDetalhesAumentoSalarialQuery: {}", query);

    var data = aumentoSalarialService.getDetalhesAumentoSalarial(query.getAumentoSalarialId());

    return ResponseEntity.ok(data);
  }

}
