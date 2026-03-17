package cv.inps.rh.configuracao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoResponseDTO;
import cv.inps.rh.configuracao.application.services.ComponenteAvaliacaoService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;

import java.time.Year;

@Component
public class GetComponenetAvaliacaoAtualQueryHandler implements QueryHandler<GetComponenetAvaliacaoAtualQuery, ResponseEntity<ComponenteAvaliacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetComponenetAvaliacaoAtualQueryHandler.class);

  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ComponenteAvaliacaoService componenteAvaliacaoService;

  public GetComponenetAvaliacaoAtualQueryHandler(
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ComponenteAvaliacaoService componenteAvaliacaoService
  ) {
    this.objetivoDetRepository = objetivoDetRepository;
    this.componenteAvaliacaoService = componenteAvaliacaoService;

  }

   @IgrpQueryHandler
  public ResponseEntity<ComponenteAvaliacaoResponseDTO> handle(GetComponenetAvaliacaoAtualQuery query) {

    LOGGER.debug("GetComponenetAvaliacaoAtualQuery: {}", query);

    var anoAtual = Year.now().getValue();

    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(anoAtual)
        .or(() -> objetivoDetRepository.findTopByOrderByAnoDescIdDesc())
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Nenhuma parametrização encontrada em RH_T_PARAM_OBJETIVO_DET"));

    return ResponseEntity.ok(componenteAvaliacaoService.obter(det.getUuid().toString()));
  }

}
