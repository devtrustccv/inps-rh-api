package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.services.PicagemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaPicagemQueryHandler implements QueryHandler<GetListaPicagemQuery, ResponseEntity<WrapperListaPicagemDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaPicagemQueryHandler.class);

  private final PicagemService picagemService;

  public GetListaPicagemQueryHandler(PicagemService picagemService) {

    this.picagemService = picagemService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaPicagemDTO> handle(GetListaPicagemQuery query) {

    LOGGER.debug("GetListaPicagemQuery: {}", query);

    return ResponseEntity.ok(picagemService.getListaPicagem(query));
  }

}
