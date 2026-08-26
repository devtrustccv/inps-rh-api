package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.DadosApoliceResponseDTO;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDadosApolicesAtivosQueryHandler implements
    QueryHandler<GetDadosApolicesAtivosQuery, ResponseEntity<List<DadosApoliceResponseDTO>>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDadosApolicesAtivosQueryHandler.class);

  private final SoatService service;

  public GetDadosApolicesAtivosQueryHandler(SoatService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<DadosApoliceResponseDTO>> handle(
      GetDadosApolicesAtivosQuery query) {
    LOGGER.debug("Getting active insurance policy data");

    return ResponseEntity.ok(service.listarDadosApoliceAtivos());
  }
}
