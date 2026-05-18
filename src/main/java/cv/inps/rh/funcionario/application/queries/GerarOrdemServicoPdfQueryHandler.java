package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.documento.OrdemServicoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GerarOrdemServicoPdfQueryHandler implements QueryHandler<GerarOrdemServicoPdfQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GerarOrdemServicoPdfQueryHandler.class);

  private final OrdemServicoReadService ordemServicoReadService;

  public GerarOrdemServicoPdfQueryHandler(OrdemServicoReadService ordemServicoReadService) {
    this.ordemServicoReadService = ordemServicoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(GerarOrdemServicoPdfQuery query) {
    LOGGER.debug("GerarOrdemServicoPdfQuery: {}", query);
    var bytes = ordemServicoReadService.gerarPdf(query.getOsUuid());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ordem-servico.pdf\"")
        .body(bytes);
  }

}
