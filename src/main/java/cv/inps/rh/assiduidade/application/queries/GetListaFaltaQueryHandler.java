package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.services.FaltaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaFaltaQueryHandler implements QueryHandler<GetListaFaltaQuery, ResponseEntity<WrapperListaFaltaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaFaltaQueryHandler.class);

  private final FaltaReadService faltaReadService;

  public GetListaFaltaQueryHandler(FaltaReadService faltaReadService) {

    this.faltaReadService = faltaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaFaltaDTO> handle(GetListaFaltaQuery query) {

    LOGGER.debug("GetListaFaltaQuery: {}", query);


    return ResponseEntity.ok(faltaReadService.faltaReadService(query));
  }

}
