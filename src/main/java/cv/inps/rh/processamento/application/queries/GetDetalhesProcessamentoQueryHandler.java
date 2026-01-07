package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalhesProcessamentoQueryHandler implements QueryHandler<GetDetalhesProcessamentoQuery, ResponseEntity<DetalhesProcessamentoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalhesProcessamentoQueryHandler.class);

  private final ProcessamentoSalarialReadService processamentoSalarialService;

  public GetDetalhesProcessamentoQueryHandler(ProcessamentoSalarialReadService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<DetalhesProcessamentoDTO> handle(GetDetalhesProcessamentoQuery query) {

    LOGGER.debug("GetDetalhesProcessamentoQuery: {}", query);

    var data = processamentoSalarialService.getDetalhesProcessamentoSalarial(query);

    return ResponseEntity.ok(data);
  }

}
