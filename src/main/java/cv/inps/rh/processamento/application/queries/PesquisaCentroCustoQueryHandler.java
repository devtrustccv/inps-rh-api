package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperPesquisaColaboradorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PesquisaCentroCustoQueryHandler implements QueryHandler<PesquisaCentroCustoQuery, ResponseEntity<WrapperPesquisaColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PesquisaCentroCustoQueryHandler.class);


  public PesquisaCentroCustoQueryHandler() {

  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperPesquisaColaboradorDTO> handle(PesquisaCentroCustoQuery query) {

    LOGGER.debug("PesquisaCentroCustoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}
