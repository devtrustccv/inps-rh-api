package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.EmprestimoListDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ListarEmprestimosQueryHandler implements QueryHandler<ListarEmprestimosQuery, ResponseEntity<EmprestimoListDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListarEmprestimosQueryHandler.class);

  private final EmprestimoReadService emprestimoReadService;

  public ListarEmprestimosQueryHandler(EmprestimoReadService emprestimoReadService) {
    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<EmprestimoListDTO> handle(ListarEmprestimosQuery query) {

    LOGGER.debug("ListarEmprestimosQuery: {}", query);

    var data = emprestimoReadService.listarEmprestimos(query);

    return ResponseEntity.ok(data);
  }

}
