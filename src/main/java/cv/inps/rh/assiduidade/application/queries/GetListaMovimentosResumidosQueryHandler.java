package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.services.MovimentoResumoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaMovimentosResumidosQueryHandler implements QueryHandler<GetListaMovimentosResumidosQuery, ResponseEntity<WrapperListaAssiduidadadeDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaMovimentosResumidosQueryHandler.class);

  private final MovimentoResumoService movimentoResumoService;

  public GetListaMovimentosResumidosQueryHandler(MovimentoResumoService movimentoResumoService) {

    this.movimentoResumoService = movimentoResumoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListaAssiduidadadeDTO> handle(GetListaMovimentosResumidosQuery query) {

    LOGGER.debug("GetListaMovimentosResumidosQuery: {}", query);

    return ResponseEntity.ok(movimentoResumoService.getListaMovimentosResumidos(query));
  }

}
