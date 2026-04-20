package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.WrapperListaManualFuncaoDTO;
import cv.inps.rh.configuracao.application.services.ManualFuncaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaManualFuncaoQueryHandler implements QueryHandler<GetListaManualFuncaoQuery, ResponseEntity<WrapperListaManualFuncaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaManualFuncaoQueryHandler.class);

  private final ManualFuncaoService manualFuncaoService;

  public GetListaManualFuncaoQueryHandler(ManualFuncaoService manualFuncaoService) {
    this.manualFuncaoService = manualFuncaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaManualFuncaoDTO> handle(GetListaManualFuncaoQuery query) {

    LOGGER.debug("GetListaManualFuncaoQuery: {}", query);

    return ResponseEntity.ok(manualFuncaoService.listar(query));
  }

}
