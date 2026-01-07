package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetHistoricoLaboralQueryHandler implements QueryHandler<GetHistoricoLaboralQuery, ResponseEntity<WrapperHistLaboralResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHistoricoLaboralQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetHistoricoLaboralQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {
    this.historicoLaboralReadService = historicoLaboralReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperHistLaboralResponseDTO> handle(GetHistoricoLaboralQuery query) {

    LOGGER.info("Handling GetHistoricoLaboralQuery: {}", query);

    var data = historicoLaboralReadService.getHistoricoLaboral2(query);

    return ResponseEntity.ok(data);
  }

}
