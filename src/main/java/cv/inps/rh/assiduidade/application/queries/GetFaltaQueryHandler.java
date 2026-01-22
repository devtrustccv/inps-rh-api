package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.FaltaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;

@Component
public class GetFaltaQueryHandler implements QueryHandler<GetFaltaQuery, ResponseEntity<FaltaReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFaltaQueryHandler.class);

  private final FaltaReadService faltaReadService;

  public GetFaltaQueryHandler(FaltaReadService faltaReadService) {

    this.faltaReadService = faltaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<FaltaReqDTO> handle(GetFaltaQuery query) {

    LOGGER.debug("GetFaltaQuery: {}", query);

    return ResponseEntity.ok(faltaReadService.getFalta(query));
  }

}
