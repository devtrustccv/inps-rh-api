package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;

@Component
public class GetRelacaoLaboralQueryHandler implements QueryHandler<GetRelacaoLaboralQuery, ResponseEntity<WrapperHistLaboralResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetRelacaoLaboralQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {

    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperHistLaboralResponseDTO> handle(GetRelacaoLaboralQuery query) {

    LOGGER.debug("GetRelacaoLaboralQuery: {}", query);


    return ResponseEntity.ok(historicoLaboralReadService.getRelacaoLaboral(query));
  }

}
