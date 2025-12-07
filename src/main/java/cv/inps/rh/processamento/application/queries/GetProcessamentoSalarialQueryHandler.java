package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperProcessamentoSalarialDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessamentoSalarialQueryHandler implements QueryHandler<GetProcessamentoSalarialQuery, ResponseEntity<WrapperProcessamentoSalarialDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessamentoSalarialQueryHandler.class);

  private final ProcessamentoSalarialReadService processamentoSalarialService;

  public GetProcessamentoSalarialQueryHandler(ProcessamentoSalarialReadService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperProcessamentoSalarialDTO> handle(GetProcessamentoSalarialQuery query) {

    LOGGER.debug("GetProcessamentoSalarialQuery: {}", query);

    var data = processamentoSalarialService.getProcessamentoSalarial(query);

    return ResponseEntity.ok(data);
  }

}
