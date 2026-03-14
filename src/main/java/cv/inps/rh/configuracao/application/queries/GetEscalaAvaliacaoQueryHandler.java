package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO;
import cv.inps.rh.configuracao.application.services.EscalaAvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetEscalaAvaliacaoQueryHandler
    implements QueryHandler<GetEscalaAvaliacaoQuery, ResponseEntity<EscalaAvaliacaoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEscalaAvaliacaoQueryHandler.class);

  private final EscalaAvaliacaoService escalaAvaliacaoService;

  public GetEscalaAvaliacaoQueryHandler(EscalaAvaliacaoService escalaAvaliacaoService) {
    this.escalaAvaliacaoService = escalaAvaliacaoService;

  }

  @IgrpQueryHandler
  public ResponseEntity<EscalaAvaliacaoResponseDTO> handle(GetEscalaAvaliacaoQuery query) {

    LOGGER.debug("GetEscalaAvaliacaoQuery: {}", query);

    return ResponseEntity.ok(escalaAvaliacaoService.obter(query));
  }

}
