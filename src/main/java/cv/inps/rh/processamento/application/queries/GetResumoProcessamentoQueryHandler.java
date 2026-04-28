package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetResumoProcessamentoQueryHandler implements QueryHandler<GetResumoProcessamentoQuery, ResponseEntity<ResumoProcessamentoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetResumoProcessamentoQueryHandler.class);

  private final ProcessamentoSalarialReadService processamentoSalarialService;

  public GetResumoProcessamentoQueryHandler(ProcessamentoSalarialReadService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ResumoProcessamentoDTO> handle(GetResumoProcessamentoQuery query) {

    LOGGER.debug("GetResumoProcessamentoQuery: {}", query);

    var data = processamentoSalarialService.getResumoProcessamentoSalarial(query.getProcessamentoId());

    return ResponseEntity.ok(data);
  }
}
