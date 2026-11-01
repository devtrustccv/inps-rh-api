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
public class GetSubsidioNatalQueryHandler implements QueryHandler<GetSubsidioNatalQuery, ResponseEntity<WrapperListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubsidioNatalQueryHandler.class);

  private final SubsidioService subsidioNatalService;

  public GetSubsidioNatalQueryHandler(SubsidioService subsidioNatalService) {
    this.subsidioNatalService = subsidioNatalService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListDTO> handle(GetSubsidioNatalQuery query) {

    LOGGER.debug("GetSubsidioNatalQuery: {}", query);

    var data = subsidioNatalService.getDataSubsidioNatal(
        query.getAnoProcessamento(),
        query.getDirecaoId(),
        query.getFuncionarioId(),
        query.getPage(),
        query.getSize()
    );

    return ResponseEntity.ok(data);
  }

}
