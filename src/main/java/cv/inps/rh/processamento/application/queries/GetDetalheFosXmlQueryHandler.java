package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.DetalhesFosXmlDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalheFosXmlQueryHandler implements QueryHandler<GetDetalheFosXmlQuery, ResponseEntity<DetalhesFosXmlDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheFosXmlQueryHandler.class);

  private final FosService fosService;

  public GetDetalheFosXmlQueryHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpQueryHandler
  public ResponseEntity<DetalhesFosXmlDTO> handle(GetDetalheFosXmlQuery query) {

    LOGGER.debug("GetDetalheFosXmlQuery: {}", query);

    var data = fosService.getFosDetalhes(query.getFosId());

    return ResponseEntity.ok(data);
  }

}
