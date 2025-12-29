package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;

@Component
public class GetHistoricoLaboralByIdQueryHandler implements QueryHandler<GetHistoricoLaboralByIdQuery, ResponseEntity<ValidarNovoHistoricoLaboralDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHistoricoLaboralByIdQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetHistoricoLaboralByIdQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {
    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> handle(GetHistoricoLaboralByIdQuery query) {

    LOGGER.debug("GetHistoricoLaboralByIdQuery: {}", query);

    var data = historicoLaboralReadService.getHistoricoLaboralById(query);
    return ResponseEntity.ok(data);
  }

}
