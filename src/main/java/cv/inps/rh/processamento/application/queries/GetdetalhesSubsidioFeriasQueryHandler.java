package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.SubsidioFeriaDetalheFullDTO;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetdetalhesSubsidioFeriasQueryHandler implements QueryHandler<GetDetalhesSubsidioFeriasQuery, ResponseEntity<SubsidioFeriaDetalheFullDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetdetalhesSubsidioFeriasQueryHandler.class);

  private final SubsidioService subsidioService;

  public GetdetalhesSubsidioFeriasQueryHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpQueryHandler
  public ResponseEntity<SubsidioFeriaDetalheFullDTO> handle(GetDetalhesSubsidioFeriasQuery query) {

    LOGGER.debug("GetDetalhesSubsidioFeriasQuery: {}", query);

    var result = subsidioService.getDetalhesSubsidio(
        query.getAno(),
        query.getFunId()
    );

    return ResponseEntity.ok(result);
  }
}
