package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;

@Component
public class GetRenumeracaoByIdQueryHandler implements QueryHandler<GetRenumeracaoByIdQuery, ResponseEntity<NovoRemuneracaoRequestDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRenumeracaoByIdQueryHandler.class);

  private final RenumeracoesReadService renumeracoesReadService;

  public GetRenumeracaoByIdQueryHandler(RenumeracoesReadService renumeracoesReadService) {

    this.renumeracoesReadService = renumeracoesReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<NovoRemuneracaoRequestDTO> handle(GetRenumeracaoByIdQuery query) {

    LOGGER.debug("GetRenumeracaoByIdQuery: {}", query);

    return ResponseEntity.ok(renumeracoesReadService.getRenumeracaoById(query));
  }

}
