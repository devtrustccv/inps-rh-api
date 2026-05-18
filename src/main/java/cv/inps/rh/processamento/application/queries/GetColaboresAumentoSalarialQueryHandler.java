package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.ColaboradorAumentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetColaboresAumentoSalarialQueryHandler implements QueryHandler<GetColaboresAumentoSalarialQuery, ResponseEntity<ColaboradorAumentoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetColaboresAumentoSalarialQueryHandler.class);

  private final AumentoSalarialReadService aumentoSalarialService;

  public GetColaboresAumentoSalarialQueryHandler(AumentoSalarialReadService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ColaboradorAumentoDTO> handle(GetColaboresAumentoSalarialQuery query) {

    LOGGER.debug("GetColaboresAumentoSalarialQuery: {}", query);

    var data = aumentoSalarialService.getColaboradores(query);

    return ResponseEntity.ok(data);
  }

}
