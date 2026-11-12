package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.DirecaoResumoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDirecoesComColaboradoresQueryHandler implements QueryHandler<GetDirecoesComColaboradoresQuery, ResponseEntity<List<DirecaoResumoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDirecoesComColaboradoresQueryHandler.class);

  private final AvaliacaoService avaliacaoService;

  public GetDirecoesComColaboradoresQueryHandler(AvaliacaoService avaliacaoService) {
    this.avaliacaoService = avaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<List<DirecaoResumoDTO>> handle(GetDirecoesComColaboradoresQuery query) {

    LOGGER.debug("GetDirecoesComColaboradoresQuery: {}", query);

    return ResponseEntity.ok(avaliacaoService.getDirecoesComColaboradores());
  }

}
