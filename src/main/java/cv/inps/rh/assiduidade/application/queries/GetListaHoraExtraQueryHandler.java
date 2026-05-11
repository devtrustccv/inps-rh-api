package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.WrapperListaHoraExtraDTO;
import cv.inps.rh.assiduidade.application.services.HoraExtraReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaHoraExtraQueryHandler implements QueryHandler<GetListaHoraExtraQuery, ResponseEntity<WrapperListaHoraExtraDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaHoraExtraQueryHandler.class);

  private final HoraExtraReadService horaExtraReadService;

  public GetListaHoraExtraQueryHandler(HoraExtraReadService horaExtraReadService) {

    this.horaExtraReadService = horaExtraReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaHoraExtraDTO> handle(GetListaHoraExtraQuery query) {

    LOGGER.debug("GetListaHoraExtraQuery: {}", query);


    return ResponseEntity.ok(horaExtraReadService.getListaHoraExtra(query));
  }

}
