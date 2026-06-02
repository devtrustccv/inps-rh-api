package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.BaixaMedicaCalculoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CalcularBaixaMedicaQueryHandler implements QueryHandler<CalcularBaixaMedicaQuery, ResponseEntity<BaixaMedicaCalculoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularBaixaMedicaQueryHandler.class);


  public CalcularBaixaMedicaQueryHandler() {

  }

  @IgrpQueryHandler
  public ResponseEntity<BaixaMedicaCalculoDTO> handle(CalcularBaixaMedicaQuery query) {

    LOGGER.debug("CalcularBaixaMedicaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}
