package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralReadService;
import cv.inps.rh.shared.application.dto.ComboItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRelacaoLaboralComboQueryHandler implements QueryHandler<GetRelacaoLaboralComboQuery, ResponseEntity<List<ComboItemDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralComboQueryHandler.class);

  private final HistoricoLaboralReadService historicoLaboralReadService;

  public GetRelacaoLaboralComboQueryHandler(HistoricoLaboralReadService historicoLaboralReadService) {

    this.historicoLaboralReadService = historicoLaboralReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ComboItemDTO>> handle(GetRelacaoLaboralComboQuery query) {

    LOGGER.debug("GetRelacaoLaboralComboQuery: {}", query);


    return ResponseEntity.ok(historicoLaboralReadService.getRelacaoLaboralCombo(query));
  }

}
