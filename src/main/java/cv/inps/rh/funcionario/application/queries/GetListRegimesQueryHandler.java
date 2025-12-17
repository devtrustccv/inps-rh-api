package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.service.RegimeReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListRegimesQueryHandler implements QueryHandler<GetListRegimesQuery, ResponseEntity<WrapperRegimeListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRegimesQueryHandler.class);


  private final RegimeReadService regimeReadService;

  public GetListRegimesQueryHandler( RegimeReadService regimeReadService) {

    this.regimeReadService = regimeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperRegimeListDTO> handle(GetListRegimesQuery query) {

    LOGGER.info("Handling GetListRegimesQuery: {}", query);

    return ResponseEntity.ok(regimeReadService.listRegime(query));
  }

}
