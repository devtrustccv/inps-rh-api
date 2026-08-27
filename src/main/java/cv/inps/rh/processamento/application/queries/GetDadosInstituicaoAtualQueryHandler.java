package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoResponseDTO;
import cv.inps.rh.processamento.domain.service.SoatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDadosInstituicaoAtualQueryHandler implements
    QueryHandler<GetDadosInstituicaoAtualQuery, ResponseEntity<DadosInstituicaoResponseDTO>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDadosInstituicaoAtualQueryHandler.class);

  private final SoatService service;

  public GetDadosInstituicaoAtualQueryHandler(SoatService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<DadosInstituicaoResponseDTO> handle(
      GetDadosInstituicaoAtualQuery query) {

    LOGGER.debug("Getting current active institution data");

    return ResponseEntity.ok(service.obterDadosInstituicaoAtual());
  }
}
