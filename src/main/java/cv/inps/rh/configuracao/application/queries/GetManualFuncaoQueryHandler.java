package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.ManualFuncaoResponseDTO;
import cv.inps.rh.configuracao.application.services.ManualFuncaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetManualFuncaoQueryHandler implements QueryHandler<GetManualFuncaoQuery, ResponseEntity<ManualFuncaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetManualFuncaoQueryHandler.class);

  private final ManualFuncaoService manualFuncaoService;

  public GetManualFuncaoQueryHandler(ManualFuncaoService manualFuncaoService) {
    this.manualFuncaoService = manualFuncaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<ManualFuncaoResponseDTO> handle(GetManualFuncaoQuery query) {

    LOGGER.debug("GetManualFuncaoQuery: {}", query);

    return ResponseEntity.ok(manualFuncaoService.obter(query));
  }

}
