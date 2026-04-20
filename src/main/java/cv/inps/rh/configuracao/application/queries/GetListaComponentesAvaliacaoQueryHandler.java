package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.WrapperListComponenteAvaliacaoDTO;
import cv.inps.rh.configuracao.application.services.ComponenteAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaComponentesAvaliacaoQueryHandler implements QueryHandler<GetListaComponentesAvaliacaoQuery, ResponseEntity<WrapperListComponenteAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaComponentesAvaliacaoQueryHandler.class);

  private final ComponenteAvaliacaoService componenteAvaliacaoService;

  public GetListaComponentesAvaliacaoQueryHandler(ComponenteAvaliacaoService componenteAvaliacaoService) {
    this.componenteAvaliacaoService = componenteAvaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListComponenteAvaliacaoDTO> handle(GetListaComponentesAvaliacaoQuery query) {

    LOGGER.debug("GetListaComponentesAvaliacaoQuery: {}", query);

    return ResponseEntity.ok(componenteAvaliacaoService.listar(query));
  }

}
