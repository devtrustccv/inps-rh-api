package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.ParamVinculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListVinculoLaboralQueryHandler implements QueryHandler<ListVinculoLaboralQuery, ResponseEntity<List<VinculoLaboralResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListVinculoLaboralQueryHandler.class);

  private final ParamVinculoService paramVinculoService;

  public ListVinculoLaboralQueryHandler(ParamVinculoService paramVinculoService) {
    this.paramVinculoService = paramVinculoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<VinculoLaboralResponseDTO>> handle(ListVinculoLaboralQuery query) {

    // TODO 13/11/2025 23:20 verify filters here

    LOGGER.debug("Page: {}, Size: {}", query.getPagina(), query.getTamanho());

    var response = paramVinculoService.getAll(query.getPagina(), query.getTamanho());

    return ResponseEntity.ok().body(response);
  }

}
