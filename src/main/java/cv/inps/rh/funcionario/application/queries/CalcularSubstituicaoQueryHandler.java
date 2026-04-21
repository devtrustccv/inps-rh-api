package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseDTO;
import cv.inps.rh.funcionario.application.service.substituicao.CalcularSubstituicaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CalcularSubstituicaoQueryHandler implements QueryHandler<CalcularSubstituicaoQuery, ResponseEntity<CalcularSubstituicaoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularSubstituicaoQueryHandler.class);

  private final CalcularSubstituicaoService calcularSubstituicaoService;

  public CalcularSubstituicaoQueryHandler(CalcularSubstituicaoService calcularSubstituicaoService) {
    this.calcularSubstituicaoService = calcularSubstituicaoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<CalcularSubstituicaoResponseDTO> handle(CalcularSubstituicaoQuery query) {

    LOGGER.info("Handling CalcularSubstituicaoQuery: {}", query);

    return ResponseEntity.ok(calcularSubstituicaoService.calcular(query));

  }

}
