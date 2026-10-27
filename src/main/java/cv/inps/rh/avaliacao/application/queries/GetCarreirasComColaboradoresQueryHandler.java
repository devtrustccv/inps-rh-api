package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetCarreirasComColaboradoresQueryHandler implements QueryHandler<GetCarreirasComColaboradoresQuery, ResponseEntity<List<CarreiraResumoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreirasComColaboradoresQueryHandler.class);

  private final AvaliacaoService avaliacaoService;

  public GetCarreirasComColaboradoresQueryHandler(AvaliacaoService avaliacaoService) {
    this.avaliacaoService = avaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<List<CarreiraResumoDTO>> handle(GetCarreirasComColaboradoresQuery query) {

    LOGGER.debug("GetCarreirasComColaboradoresQuery: {}", query);

    return ResponseEntity.ok(avaliacaoService.getCarreirasComColaboradores(query.getInstitId()));
  }

}
