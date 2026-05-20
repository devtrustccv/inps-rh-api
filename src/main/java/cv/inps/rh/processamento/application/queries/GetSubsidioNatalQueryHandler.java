package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetSubsidioNatalQueryHandler implements QueryHandler<GetSubsidioNatalQuery, ResponseEntity<List<SubsidioResponseNatalDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubsidioNatalQueryHandler.class);

  private final SubsidioService subsidioNatalService;

  public GetSubsidioNatalQueryHandler(SubsidioService subsidioNatalService) {
    this.subsidioNatalService = subsidioNatalService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<SubsidioResponseNatalDTO>> handle(GetSubsidioNatalQuery query) {

    LOGGER.debug("GetSubsidioNatalQuery: {}", query);

    var data = subsidioNatalService.getDataSubsidioNatal(
        query.getDirecaoId(),
        query.getFuncionarioId(),
        Optional.ofNullable(query.getValorBrinde()).map(Double::valueOf).orElse(null),
        query.getAnoProcessamento()
    );

    return ResponseEntity.ok(data);
  }

}
