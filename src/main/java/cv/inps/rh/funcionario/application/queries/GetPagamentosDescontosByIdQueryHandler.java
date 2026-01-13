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
public class GetPagamentosDescontosByIdQueryHandler implements QueryHandler<GetPagamentosDescontosByIdQuery, ResponseEntity<NovoRemuneracaoRequestDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPagamentosDescontosByIdQueryHandler.class);

  private final RenumeracoesReadService renumeracoesReadService;

  public GetPagamentosDescontosByIdQueryHandler(RenumeracoesReadService renumeracoesReadService) {

    this.renumeracoesReadService = renumeracoesReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<NovoRemuneracaoRequestDTO> handle(GetPagamentosDescontosByIdQuery query) {

    LOGGER.debug("GetPagamentosDescontosByIdQuery: {}", query);

    return ResponseEntity.ok(renumeracoesReadService.getPagamentoById(query));
  }

}
