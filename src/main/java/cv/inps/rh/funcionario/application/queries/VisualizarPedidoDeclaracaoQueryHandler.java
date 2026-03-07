package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.DeclaracaoReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class VisualizarPedidoDeclaracaoQueryHandler implements QueryHandler<VisualizarPedidoDeclaracaoQuery, ResponseEntity<String>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(VisualizarPedidoDeclaracaoQueryHandler.class);
  private final DeclaracaoReportService declaracaoReportService;

   @IgrpQueryHandler
  public ResponseEntity<String> handle(VisualizarPedidoDeclaracaoQuery query) {

    LOGGER.debug("VisualizarPedidoDeclaracaoQuery: {}", query);

    String reportContent = declaracaoReportService.gerarDeclaracao(query.getId());

    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(reportContent);
  }

}
