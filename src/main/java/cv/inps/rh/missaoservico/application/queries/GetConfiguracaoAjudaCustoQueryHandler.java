package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.ParamAjudaCustoDTO;
import cv.inps.rh.missaoservico.application.services.ParamAjudaCustoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetConfiguracaoAjudaCustoQueryHandler implements QueryHandler<GetConfiguracaoAjudaCustoQuery, ResponseEntity<List<ParamAjudaCustoDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetConfiguracaoAjudaCustoQueryHandler.class);

  private final ParamAjudaCustoService paramAjudaCustoService;

  public GetConfiguracaoAjudaCustoQueryHandler(ParamAjudaCustoService paramAjudaCustoService) {
    this.paramAjudaCustoService = paramAjudaCustoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ParamAjudaCustoDTO>> handle(GetConfiguracaoAjudaCustoQuery query) {
    LOGGER.debug("GetConfiguracaoAjudaCustoQuery: {}", query);
    return paramAjudaCustoService.listar();
  }

}
