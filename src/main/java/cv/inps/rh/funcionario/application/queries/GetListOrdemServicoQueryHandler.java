package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListOrdemServicoDTO;
import cv.inps.rh.funcionario.application.service.documento.OrdemServicoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListOrdemServicoQueryHandler implements QueryHandler<GetListOrdemServicoQuery, ResponseEntity<WrapperListOrdemServicoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListOrdemServicoQueryHandler.class);

  private final OrdemServicoReadService ordemServicoReadService;

  public GetListOrdemServicoQueryHandler(OrdemServicoReadService ordemServicoReadService) {
    this.ordemServicoReadService = ordemServicoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListOrdemServicoDTO> handle(GetListOrdemServicoQuery query) {
    LOGGER.debug("GetListOrdemServicoQuery: {}", query);
    return ResponseEntity.ok(ordemServicoReadService.listar(query));
  }

}
