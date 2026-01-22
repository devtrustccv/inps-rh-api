package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.MapaFeriaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaMapaFeriaDTO;

@Component
public class ListaMapaFeriaQueryHandler implements QueryHandler<ListaMapaFeriaQuery, ResponseEntity<WrapperListaMapaFeriaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ListaMapaFeriaQueryHandler.class);

 private final MapaFeriaReadService mapaFeriaReadService;

  public ListaMapaFeriaQueryHandler(MapaFeriaReadService mapaFeriaReadService) {

    this.mapaFeriaReadService = mapaFeriaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaMapaFeriaDTO> handle(ListaMapaFeriaQuery query) {

    LOGGER.debug("ListaMapaFeriaQuery: {}", query);


    return ResponseEntity.ok(mapaFeriaReadService.getListaMapaFeria(query));
  }

}
