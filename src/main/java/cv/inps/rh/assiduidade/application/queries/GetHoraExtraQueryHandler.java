package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.HoraExtraReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;

@Component
public class GetHoraExtraQueryHandler implements QueryHandler<GetHoraExtraQuery, ResponseEntity<HoraExtraReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHoraExtraQueryHandler.class);

  private final HoraExtraReadService horaExtraReadService;

  public GetHoraExtraQueryHandler(HoraExtraReadService horaExtraReadService) {

    this.horaExtraReadService = horaExtraReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<HoraExtraReqDTO> handle(GetHoraExtraQuery query) {

    LOGGER.debug("GetHoraExtraQuery: {}", query);

    return ResponseEntity.ok(horaExtraReadService.getHoraExtra(query));
  }

}
