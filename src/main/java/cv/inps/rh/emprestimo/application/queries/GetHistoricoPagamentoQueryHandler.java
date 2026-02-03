package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.HistoricoPagamentoDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetHistoricoPagamentoQueryHandler implements QueryHandler<GetHistoricoPagamentoQuery, ResponseEntity<HistoricoPagamentoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHistoricoPagamentoQueryHandler.class);

  private final EmprestimoReadService emprestimoReadService;

  public GetHistoricoPagamentoQueryHandler(EmprestimoReadService emprestimoReadService) {
    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<HistoricoPagamentoDTO> handle(GetHistoricoPagamentoQuery query) {

    LOGGER.debug("GetHistoricoPagamentoQuery: {}", query);

    var data = emprestimoReadService.getHistoricoPagamento(query.getEmprestimoId());

    return ResponseEntity.ok(data);
  }

}
