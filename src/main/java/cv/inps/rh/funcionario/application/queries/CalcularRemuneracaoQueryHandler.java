package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoResponseDTO;
import cv.inps.rh.funcionario.application.service.remuneracao.CalcularRemuneracaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CalcularRemuneracaoQueryHandler implements QueryHandler<CalcularRemuneracaoQuery, ResponseEntity<CalcularRemuneracaoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularRemuneracaoQueryHandler.class);

  private final CalcularRemuneracaoService calcularRemuneracaoService;

  public CalcularRemuneracaoQueryHandler(CalcularRemuneracaoService calcularRemuneracaoService) {
    this.calcularRemuneracaoService = calcularRemuneracaoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<CalcularRemuneracaoResponseDTO> handle(CalcularRemuneracaoQuery query) {

    LOGGER.info("Handling CalcularRemuneracaoQuery: {}", query);

    return ResponseEntity.ok(calcularRemuneracaoService.calcular(query.getRequest()));

  }

}
