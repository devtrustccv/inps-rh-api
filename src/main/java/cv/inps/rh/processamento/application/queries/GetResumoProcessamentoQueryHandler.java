package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetResumoProcessamentoQueryHandler implements QueryHandler<GetResumoProcessamentoQuery, ResponseEntity<ResumoProcessamentoDTO>> {

  private final ProcessamentoSalarialReadService processamentoSalarialService;

  public GetResumoProcessamentoQueryHandler(ProcessamentoSalarialReadService processamentoSalarialService) {
    this.processamentoSalarialService = processamentoSalarialService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ResumoProcessamentoDTO> handle(GetResumoProcessamentoQuery query) {

    var data = processamentoSalarialService.getResumoProcessamentoSalarial();

    return ResponseEntity.ok(data);
  }

}
