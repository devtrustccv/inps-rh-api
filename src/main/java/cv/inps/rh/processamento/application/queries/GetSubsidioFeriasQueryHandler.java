package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetSubsidioFeriasQueryHandler implements QueryHandler<GetSubsidioFeriasQuery, ResponseEntity<WrapperListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubsidioFeriasQueryHandler.class);

  private final SubsidioService subsidioService;

  public GetSubsidioFeriasQueryHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListDTO> handle(GetSubsidioFeriasQuery query) {

    LOGGER.debug("GetSubsidioFeriasQuery: {}", query);

    var result = subsidioService.getSubsidioFeriasData(
        query.getAno(),
        query.getDirecaoId(),
        query.getFuncionarioId(),
        query.getPage(),
        query.getSize()
    );

    return ResponseEntity.ok(result);
  }
}
