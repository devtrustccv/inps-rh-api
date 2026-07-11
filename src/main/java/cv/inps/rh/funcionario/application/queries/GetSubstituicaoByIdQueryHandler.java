package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDetalheDTO;
import cv.inps.rh.funcionario.application.service.SubstituicaoReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetSubstituicaoByIdQueryHandler implements QueryHandler<GetSubstituicaoByIdQuery, ResponseEntity<SubstituicaoDetalheDTO>> {

  private final SubstituicaoReadService substituicaoReadService;

  public GetSubstituicaoByIdQueryHandler(SubstituicaoReadService substituicaoReadService) {
    this.substituicaoReadService = substituicaoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<SubstituicaoDetalheDTO> handle(GetSubstituicaoByIdQuery query) {
    return ResponseEntity.ok(substituicaoReadService.getById(query));
  }

}
