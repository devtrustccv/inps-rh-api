package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.domain.service.baixamedica.BaixaMedicaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaBaixamedicaQueryHandler implements QueryHandler<GetListaBaixamedicaQuery, ResponseEntity<WrapperListaColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaBaixamedicaQueryHandler.class);
  private final BaixaMedicaReadService baixaMedicaReadService;

  public GetListaBaixamedicaQueryHandler(BaixaMedicaReadService baixaMedicaReadService) {
    this.baixaMedicaReadService = baixaMedicaReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListaColaboradorDTO> handle(GetListaBaixamedicaQuery query) {

    LOGGER.debug("GetListaBaixamedicaQuery: {}", query);

    var data = baixaMedicaReadService.getListaBaixaMedica(query);

    return ResponseEntity.ok(data);
  }

}
