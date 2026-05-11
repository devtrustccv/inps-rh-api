package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.DetalheMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.services.MapaFeriaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDetalheMapaFeriaQueryHandler implements QueryHandler<GetDetalheMapaFeriaQuery, ResponseEntity<DetalheMapaFeriaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheMapaFeriaQueryHandler.class);

  private final MapaFeriaReadService mapaFeriaReadService;

  public GetDetalheMapaFeriaQueryHandler(MapaFeriaReadService mapaFeriaReadService) {

    this.mapaFeriaReadService = mapaFeriaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<DetalheMapaFeriaDTO> handle(GetDetalheMapaFeriaQuery query) {

    LOGGER.debug("GetDetalheMapaFeriaQuery: {}", query);

    return ResponseEntity.ok(mapaFeriaReadService.getDetalheMapaFeria(query));
  }

}
