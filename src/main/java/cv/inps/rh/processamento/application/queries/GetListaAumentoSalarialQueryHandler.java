package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.AumentoListDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaAumentoSalarialQueryHandler implements QueryHandler<GetListaAumentoSalarialQuery, ResponseEntity<AumentoListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaAumentoSalarialQueryHandler.class);

  private final AumentoSalarialReadService aumentoSalarialService;

  public GetListaAumentoSalarialQueryHandler(AumentoSalarialReadService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<AumentoListDTO> handle(GetListaAumentoSalarialQuery query) {

    LOGGER.debug("GetListaAumentoSalarialQuery: {}", query);

    var data = aumentoSalarialService.getProcessamentoSalarial(query);

    return ResponseEntity.ok(data);
  }

}
