package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListRenumeracoesQueryHandler implements QueryHandler<GetListRenumeracoesQuery, ResponseEntity<WrapperListRenumeracaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRenumeracoesQueryHandler.class);

  private final RenumeracoesReadService renumeracoesReadService;

  public GetListRenumeracoesQueryHandler(RenumeracoesReadService renumeracoesReadService) {

    this.renumeracoesReadService = renumeracoesReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListRenumeracaoDTO> handle(GetListRenumeracoesQuery query) {

    LOGGER.info("Handling GetListRenumeracoesQuery: {}", query);

    return ResponseEntity.ok(renumeracoesReadService.getListRenumeracoes(query));

  }

}
