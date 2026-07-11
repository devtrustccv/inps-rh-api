package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.RegimeDetalheDTO;
import cv.inps.rh.funcionario.application.service.RegimeReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetRegimeByIdQueryHandler implements QueryHandler<GetRegimeByIdQuery, ResponseEntity<RegimeDetalheDTO>> {

  private final RegimeReadService regimeReadService;

  public GetRegimeByIdQueryHandler(RegimeReadService regimeReadService) {
    this.regimeReadService = regimeReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<RegimeDetalheDTO> handle(GetRegimeByIdQuery query) {
    return ResponseEntity.ok(regimeReadService.getById(query.getRegimeId()));
  }

}
