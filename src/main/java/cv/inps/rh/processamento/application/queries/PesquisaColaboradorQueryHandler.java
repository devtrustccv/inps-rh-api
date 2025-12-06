package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperPesquisaColaboradorDTO;
import cv.inps.rh.processamento.domain.service.pesquisa.PesquisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class PesquisaColaboradorQueryHandler implements QueryHandler<PesquisaColaboradorQuery, ResponseEntity<WrapperPesquisaColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PesquisaColaboradorQueryHandler.class);

  private final PesquisaService pesquisaService;

  public PesquisaColaboradorQueryHandler(PesquisaService pesquisaService) {
    this.pesquisaService = pesquisaService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperPesquisaColaboradorDTO> handle(PesquisaColaboradorQuery query) {

    LOGGER.debug("PesquisaColaboradorQuery: {}", query);

    var data = pesquisaService.pesquisaColaborador(query);

    return ResponseEntity.ok(data);
  }

}
