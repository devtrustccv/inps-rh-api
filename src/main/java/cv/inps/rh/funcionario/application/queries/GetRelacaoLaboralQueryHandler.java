package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperRelacaoLaboralSumaryDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetRelacaoLaboralQueryHandler implements QueryHandler<GetRelacaoLaboralQuery, ResponseEntity<WrapperRelacaoLaboralSumaryDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetRelacaoLaboralQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {

    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperRelacaoLaboralSumaryDTO> handle(GetRelacaoLaboralQuery query) {

    LOGGER.debug("GetRelacaoLaboralQuery: {}", query);


    return ResponseEntity.ok(historicoLaboralReadService.getRelacaoLaboral(query));
  }

}
