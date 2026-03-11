package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.FeriadoListRequestDTO;
import cv.inps.rh.configuracao.application.services.FeriadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetFeriadosPorAnoQueryHandler implements QueryHandler<GetFeriadosPorAnoQuery, ResponseEntity<FeriadoListRequestDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFeriadosPorAnoQueryHandler.class);

  private final FeriadoService feriadoService;

  public GetFeriadosPorAnoQueryHandler(FeriadoService feriadoService) {
    this.feriadoService = feriadoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<FeriadoListRequestDTO> handle(GetFeriadosPorAnoQuery query) {

    LOGGER.debug("GetFeriadosPorAnoQuery: {}", query);

    var data = feriadoService.getFeriados(query.getAnoReferente());

    return ResponseEntity.ok(data);
  }

}
