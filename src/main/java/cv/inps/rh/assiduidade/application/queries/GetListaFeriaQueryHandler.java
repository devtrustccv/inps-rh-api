package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;
import cv.inps.rh.assiduidade.application.services.FeriaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaFeriaQueryHandler implements QueryHandler<GetListaFeriaQuery, ResponseEntity<WrapperListaFeriaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaFeriaQueryHandler.class);

   private final FeriaReadService feriaReadService;

  public GetListaFeriaQueryHandler(FeriaReadService feriaReadService) {

    this.feriaReadService = feriaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaFeriaDTO> handle(GetListaFeriaQuery query) {

    LOGGER.debug("GetListaFeriaQuery: {}", query);

    return ResponseEntity.ok(feriaReadService.getListaFeria(query));
  }

}
