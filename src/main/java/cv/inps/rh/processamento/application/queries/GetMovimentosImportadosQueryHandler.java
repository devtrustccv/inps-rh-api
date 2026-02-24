package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.MovimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMovimentosImportadosQueryHandler implements QueryHandler<GetMovimentosImportadosQuery, ResponseEntity<MovimentosImportadosDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMovimentosImportadosQueryHandler.class);

  private final MovimentoService movimentoService;

  public GetMovimentosImportadosQueryHandler(MovimentoService movimentoService) {
    this.movimentoService = movimentoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<MovimentosImportadosDTO> handle(GetMovimentosImportadosQuery query) {

    LOGGER.debug("GetMovimentosImportadosQuery: {}", query);

    var data = movimentoService.getMovimentos(query);

    return ResponseEntity.ok(data);
  }

}
