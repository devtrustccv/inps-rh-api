package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperPesquisaCentroCustoDTO;
import cv.inps.rh.processamento.domain.service.pesquisa.PesquisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PesquisaCentroCustoQueryHandler implements QueryHandler<PesquisaCentroCustoQuery, ResponseEntity<WrapperPesquisaCentroCustoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PesquisaCentroCustoQueryHandler.class);

  private final PesquisaService pesquisaService;

  public PesquisaCentroCustoQueryHandler(PesquisaService pesquisaService) {
    this.pesquisaService = pesquisaService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperPesquisaCentroCustoDTO> handle(PesquisaCentroCustoQuery query) {

    LOGGER.debug("PesquisaCentroCustoQuery: {}", query);

    var data = pesquisaService.pesquisaCentroCusto(query);

    return ResponseEntity.ok(data);
  }

}
