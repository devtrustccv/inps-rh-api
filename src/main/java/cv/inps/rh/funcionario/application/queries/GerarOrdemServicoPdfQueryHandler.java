package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.documento.OrdemServicoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GerarOrdemServicoPdfQueryHandler implements QueryHandler<GerarOrdemServicoPdfQuery, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GerarOrdemServicoPdfQueryHandler.class);

  private final OrdemServicoReadService ordemServicoReadService;

  public GerarOrdemServicoPdfQueryHandler(OrdemServicoReadService ordemServicoReadService) {
    this.ordemServicoReadService = ordemServicoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<String> handle(GerarOrdemServicoPdfQuery query) {
    LOGGER.debug("GerarOrdemServicoPdfQuery: {}", query);
    return ResponseEntity.ok(ordemServicoReadService.gerarPdf(query.getOsUuid()));
  }

}
