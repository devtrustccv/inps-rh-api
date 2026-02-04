package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetPlanoFinanceiroQueryHandler implements QueryHandler<GetPlanoFinanceiroQuery, ResponseEntity<PlanoFinanceiroDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPlanoFinanceiroQueryHandler.class);

  private final EmprestimoReadService emprestimoReadService;

  public GetPlanoFinanceiroQueryHandler(EmprestimoReadService emprestimoReadService) {
    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<PlanoFinanceiroDTO> handle(GetPlanoFinanceiroQuery query) {

    LOGGER.debug("GetPlanoFinanceiroQuery: {}", query);

    var data = emprestimoReadService.getPlanoFinanceiro(query.getEmprestimoId());

    return ResponseEntity.ok(data);
  }

}
