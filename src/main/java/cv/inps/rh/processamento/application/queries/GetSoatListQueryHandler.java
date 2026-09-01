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
public class GetSoatListQueryHandler implements QueryHandler<GetSoatListQuery, ResponseEntity<WrapperListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSoatListQueryHandler.class);

  private final SoatService service;

  public GetSoatListQueryHandler(SoatService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListDTO> handle(GetSoatListQuery query) {

    LOGGER.debug("GetSoatListQuery: {}", query);

    var result = service.listSoat(
        query.getAnoReferente(),
        query.getMesReferente(),
        query.getPage(),
        query.getSize()
    );

    return ResponseEntity.ok(result);
  }

}
