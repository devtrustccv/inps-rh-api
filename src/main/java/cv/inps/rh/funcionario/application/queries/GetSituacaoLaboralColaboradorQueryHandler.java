package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.application.service.SituacaoLaboralColaboradorReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetSituacaoLaboralColaboradorQueryHandler implements QueryHandler<GetSituacaoLaboralColaboradorQuery, ResponseEntity<AtivarInativarColaboradorDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSituacaoLaboralColaboradorQueryHandler.class);

  private final SituacaoLaboralColaboradorReadService situacaoLaboralColaboradorReadService;

  public GetSituacaoLaboralColaboradorQueryHandler(SituacaoLaboralColaboradorReadService situacaoLaboralColaboradorReadService) {
    this.situacaoLaboralColaboradorReadService = situacaoLaboralColaboradorReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<AtivarInativarColaboradorDTO> handle(GetSituacaoLaboralColaboradorQuery query) {
    LOGGER.info("Handling GetSituacaoLaboralColaboradorQuery: {}", query);
    var situacaoLaboral = situacaoLaboralColaboradorReadService.execute(query);
    return ResponseEntity.ok(situacaoLaboral);
  }

}
