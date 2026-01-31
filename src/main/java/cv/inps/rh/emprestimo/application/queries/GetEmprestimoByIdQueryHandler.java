package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetEmprestimoByIdQueryHandler implements QueryHandler<GetEmprestimoByIdQuery, ResponseEntity<PedidoEmprestimoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEmprestimoByIdQueryHandler.class);

  private final EmprestimoReadService emprestimoReadService;

  public GetEmprestimoByIdQueryHandler(EmprestimoReadService emprestimoReadService) {

    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<PedidoEmprestimoDTO> handle(GetEmprestimoByIdQuery query) {

    LOGGER.debug("GetEmprestimoByIdQuery: {}", query);

    var data = emprestimoReadService.getPedidoEmprestimoByUuid(query.getEmprestimoId());

    return ResponseEntity.ok(data);
  }

}
