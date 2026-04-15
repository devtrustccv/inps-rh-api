package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaFosQueryHandler implements QueryHandler<GetListaFosQuery, ResponseEntity<ListaFosDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaFosQueryHandler.class);

  private final FosService fosService;

  public GetListaFosQueryHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ListaFosDTO> handle(GetListaFosQuery query) {

    LOGGER.debug("GetListaFosQuery: {}", query);

    var data = fosService.getListaFos(query);

    return ResponseEntity.ok(data);
  }
}
