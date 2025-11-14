package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.TipoContratoLaboralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListTipoContratoLaboralQueryHandler implements QueryHandler<ListTipoContratoLaboralQuery, ResponseEntity<List<TipoContratoLaboralResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListTipoContratoLaboralQueryHandler.class);

  private final TipoContratoLaboralService tipoContratoLaboralService;

  public ListTipoContratoLaboralQueryHandler(TipoContratoLaboralService tipoContratoLaboralService) {
    this.tipoContratoLaboralService = tipoContratoLaboralService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<TipoContratoLaboralResponseDTO>> handle(ListTipoContratoLaboralQuery query) {

    // TODO 13/11/2025 23:18 add description filter here

    LOGGER.debug("Page: {}, Size: {}", query.getPagina(), query.getTamanho());

    var response = tipoContratoLaboralService.getAll(query.getPagina(), query.getTamanho());

    return ResponseEntity.ok().body(response);
  }

}
