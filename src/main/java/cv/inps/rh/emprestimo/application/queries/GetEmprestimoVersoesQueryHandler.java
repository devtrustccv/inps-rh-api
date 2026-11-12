package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.EmprestimoVersaoDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetEmprestimoVersoesQueryHandler implements QueryHandler<GetEmprestimoVersoesQuery, ResponseEntity<List<EmprestimoVersaoDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEmprestimoVersoesQueryHandler.class);

  private final EmprestimoReadService emprestimoReadService;

  public GetEmprestimoVersoesQueryHandler(EmprestimoReadService emprestimoReadService) {
    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<EmprestimoVersaoDTO>> handle(GetEmprestimoVersoesQuery query) {

    LOGGER.debug("GetEmprestimoVersoesQuery: {}", query);

    return ResponseEntity.ok(emprestimoReadService.getVersoes(query.getEmprestimoId()));
  }

}
