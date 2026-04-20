package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.WrapperListaEscalaAvaliacaoDTO;
import cv.inps.rh.configuracao.application.services.EscalaAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaEscalaAvaliacaoQueryHandler implements QueryHandler<GetListaEscalaAvaliacaoQuery, ResponseEntity<WrapperListaEscalaAvaliacaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaEscalaAvaliacaoQueryHandler.class);

  private final EscalaAvaliacaoService escalaAvaliacaoService;

  public GetListaEscalaAvaliacaoQueryHandler(EscalaAvaliacaoService escalaAvaliacaoService) {
    this.escalaAvaliacaoService = escalaAvaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaEscalaAvaliacaoDTO> handle(GetListaEscalaAvaliacaoQuery query) {

    LOGGER.debug("GetListaEscalaAvaliacaoQuery: {}", query);

    return ResponseEntity.ok(escalaAvaliacaoService.listar(query));
  }

}
