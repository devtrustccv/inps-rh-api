package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalhesSoatQueryHandler implements QueryHandler<GetDetalhesSoatQuery, ResponseEntity<WrapperListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalhesSoatQueryHandler.class);

  private final SoatService service;

  public GetDetalhesSoatQueryHandler(SoatService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListDTO> handle(GetDetalhesSoatQuery query) {

    LOGGER.debug("GetListaAumentoSalarialQuery: {}", query);

    var data = service.getDetalhesSoat(query.getSoatId(), query.getPage(), query.getSize());

    return ResponseEntity.ok(data);
  }

}
