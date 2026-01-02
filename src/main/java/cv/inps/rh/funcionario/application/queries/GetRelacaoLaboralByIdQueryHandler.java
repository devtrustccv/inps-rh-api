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
public class GetRelacaoLaboralByIdQueryHandler implements QueryHandler<GetRelacaoLaboralByIdQuery, ResponseEntity<ValidarNovoHistoricoLaboralDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralByIdQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetRelacaoLaboralByIdQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {
    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> handle(GetRelacaoLaboralByIdQuery query) {

    LOGGER.debug("GetHistoricoLaboralByIdQuery: {}", query);

    var data = historicoLaboralReadService.getRelacaoLaboralById(query);
    return ResponseEntity.ok(data);
  }

}
