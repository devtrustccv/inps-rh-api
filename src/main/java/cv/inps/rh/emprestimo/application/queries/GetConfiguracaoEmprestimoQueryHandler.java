package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetConfiguracaoEmprestimoQueryHandler implements QueryHandler<GetConfiguracaoEmprestimoQuery, ResponseEntity<List<InformacaoEmprestimoRequestDTO>>> {

  private final EmprestimoReadService emprestimoReadService;

  public GetConfiguracaoEmprestimoQueryHandler(EmprestimoReadService emprestimoReadService) {
    this.emprestimoReadService = emprestimoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<InformacaoEmprestimoRequestDTO>> handle(GetConfiguracaoEmprestimoQuery query) {

    var data = emprestimoReadService.getAllConfiguracaoEmprestimo();

    return ResponseEntity.ok(data);
  }

}
