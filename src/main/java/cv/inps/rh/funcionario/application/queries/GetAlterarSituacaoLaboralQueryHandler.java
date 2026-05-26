package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.AlterarSituacaoLaboralRequest;
import cv.inps.rh.funcionario.application.service.AlterarSituacaoLaboralReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAlterarSituacaoLaboralQueryHandler implements QueryHandler<GetAlterarSituacaoLaboralQuery, ResponseEntity<AlterarSituacaoLaboralRequest>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAlterarSituacaoLaboralQueryHandler.class);

  private final AlterarSituacaoLaboralReadService alterarSituacaoLaboralReadService;

  public GetAlterarSituacaoLaboralQueryHandler(AlterarSituacaoLaboralReadService alterarSituacaoLaboralReadService) {
    this.alterarSituacaoLaboralReadService = alterarSituacaoLaboralReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<AlterarSituacaoLaboralRequest> handle(GetAlterarSituacaoLaboralQuery query) {
    LOGGER.info("Handling GetAlterarSituacaoLaboralQuery: {}", query.getIdFuncionario());
    return ResponseEntity.ok(alterarSituacaoLaboralReadService.execute(query));
  }

}
