package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.DeclaracaoReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class VisualizarPedidoDeclaracaoQueryHandler implements QueryHandler<VisualizarPedidoDeclaracaoQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(VisualizarPedidoDeclaracaoQueryHandler.class);
  private final DeclaracaoReportService declaracaoReportService;

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(VisualizarPedidoDeclaracaoQuery query) {

    LOGGER.debug("VisualizarPedidoDeclaracaoQuery: {}", query);

    byte[] pdf = declaracaoReportService.gerarDeclaracao(query.getId());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=declaracao.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(pdf.length)
        .body(pdf);
  }

}
