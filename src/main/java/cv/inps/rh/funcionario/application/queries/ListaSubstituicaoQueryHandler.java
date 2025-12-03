package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.SubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.service.SubstituicaoReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListaSubstituicaoQueryHandler implements QueryHandler<ListaSubstituicaoQuery, ResponseEntity<List<SubstituicaoSumaryDTO>>> {

  private final SubstituicaoReadService substituicaoReadService;

  public ListaSubstituicaoQueryHandler(SubstituicaoReadService substituicaoReadService) {

    this.substituicaoReadService = substituicaoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<SubstituicaoSumaryDTO>> handle(ListaSubstituicaoQuery query) {
    return ResponseEntity.ok(substituicaoReadService.listar(query));
  }

}
