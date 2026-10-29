package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetCarreiraDoColaboradorQueryHandler implements QueryHandler<GetCarreiraDoColaboradorQuery, ResponseEntity<CarreiraResumoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraDoColaboradorQueryHandler.class);

  private final AvaliacaoService avaliacaoService;

  public GetCarreiraDoColaboradorQueryHandler(AvaliacaoService avaliacaoService) {
    this.avaliacaoService = avaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<CarreiraResumoDTO> handle(GetCarreiraDoColaboradorQuery query) {

    LOGGER.debug("GetCarreiraDoColaboradorQuery: {}", query);

    // Sem carreira identificável devolve um DTO vazio (id/nome nulos), não 404.
    return ResponseEntity.ok(avaliacaoService.getCarreiraDoColaborador(query.getFunUuid()));
  }

}
