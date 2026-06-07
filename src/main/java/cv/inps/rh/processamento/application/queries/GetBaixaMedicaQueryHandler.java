package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.BaixaMedicaDetailDTO;
import cv.inps.rh.processamento.domain.service.baixamedica.BaixaMedicaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GetBaixaMedicaQueryHandler
    implements QueryHandler<GetBaixaMedicaQuery, ResponseEntity<BaixaMedicaDetailDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetBaixaMedicaQueryHandler.class);

  private final BaixaMedicaReadService baixaMedicaReadService;

  public GetBaixaMedicaQueryHandler(BaixaMedicaReadService baixaMedicaReadService) {
    this.baixaMedicaReadService = baixaMedicaReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<BaixaMedicaDetailDTO> handle(GetBaixaMedicaQuery query) {

    LOGGER.debug("GetBaixaMedicaQuery: {}", query);

    return ResponseEntity.ok(baixaMedicaReadService.getBaixaMedica(query));
  }

}
