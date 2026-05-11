package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.VerMapaDTO;
import cv.inps.rh.assiduidade.application.services.MapaFeriaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerMapaQueryHandler implements QueryHandler<VerMapaQuery, ResponseEntity<VerMapaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(VerMapaQueryHandler.class);

  private final MapaFeriaReadService mapaFeriaReadService;
  public VerMapaQueryHandler(MapaFeriaReadService mapaFeriaReadService) {

    this.mapaFeriaReadService = mapaFeriaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<VerMapaDTO> handle(VerMapaQuery query) {

    LOGGER.debug("VerMapaQuery: {}", query);


    return ResponseEntity.ok(mapaFeriaReadService.verMapa(query));
  }

}
