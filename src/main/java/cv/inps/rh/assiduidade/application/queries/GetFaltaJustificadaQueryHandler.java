package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.JustificarFaltaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;

@Component
public class GetFaltaJustificadaQueryHandler implements QueryHandler<GetFaltaJustificadaQuery, ResponseEntity<JustificarFaltaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFaltaJustificadaQueryHandler.class);

  private final JustificarFaltaReadService justificarFaltaReadService;

  public GetFaltaJustificadaQueryHandler(JustificarFaltaReadService justificarFaltaReadService) {

    this.justificarFaltaReadService = justificarFaltaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<JustificarFaltaDTO> handle(GetFaltaJustificadaQuery query) {

    LOGGER.debug("GetFaltaJustificadaQuery: {}", query);

    return ResponseEntity.ok(justificarFaltaReadService.getFaltaJustificada(query));
  }

}
