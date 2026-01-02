package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;

@Component
public class GetRelacaoLaboralByCarreiraIdQueryHandler implements QueryHandler<GetRelacaoLaboralByCarreiraIdQuery, ResponseEntity<RelacaoLaboralDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralByCarreiraIdQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetRelacaoLaboralByCarreiraIdQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {
    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<RelacaoLaboralDTO> handle(GetRelacaoLaboralByCarreiraIdQuery query) {

    LOGGER.debug("GetHistoricoLaboralByIdQuery: {}", query);

    var data = historicoLaboralReadService.getRelacaoLaboralById(query);
    return ResponseEntity.ok(data);
  }

}
