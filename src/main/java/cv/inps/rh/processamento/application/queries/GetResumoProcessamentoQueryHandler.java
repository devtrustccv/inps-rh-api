package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.ProcessamentoSalarialReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    var procId = StringUtils.hasText(query.getProcessamentoId()) ? Long.valueOf(query.getProcessamentoId()) : null;
    var ccId = StringUtils.hasText(query.getCcId()) ? Long.valueOf(query.getCcId()) : null;
    var ano = StringUtils.hasText(query.getAno()) ? Integer.valueOf(query.getAno()) : null;
    var mes = StringUtils.hasText(query.getMes()) ? Integer.valueOf(query.getMes()) : null;

    var data = processamentoSalarialService.getResumoProcessamentoSalarial(procId, ccId, ano, mes);

    return ResponseEntity.ok(data);
  }
}
