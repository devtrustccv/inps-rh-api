package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperSubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.service.SubstituicaoReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ListaSubstituicaoQueryHandler implements QueryHandler<ListaSubstituicaoQuery, ResponseEntity<WrapperSubstituicaoSumaryDTO>> {

  private final SubstituicaoReadService substituicaoReadService;

  public ListaSubstituicaoQueryHandler(SubstituicaoReadService substituicaoReadService) {

    this.substituicaoReadService = substituicaoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperSubstituicaoSumaryDTO> handle(ListaSubstituicaoQuery query) {
    return ResponseEntity.ok(substituicaoReadService.listar(query));
  }

}
