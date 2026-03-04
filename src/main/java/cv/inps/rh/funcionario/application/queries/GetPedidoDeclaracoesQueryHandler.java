package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoReadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListaPedidoDeclaracaoDTO;

@Component
@RequiredArgsConstructor
public class GetPedidoDeclaracoesQueryHandler implements QueryHandler<GetPedidoDeclaracoesQuery, ResponseEntity<WrapperListaPedidoDeclaracaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoDeclaracoesQueryHandler.class);
  private final PedidoDeclaracaoReadService pedidoDeclaracaoReadService;


   @IgrpQueryHandler
  public ResponseEntity<WrapperListaPedidoDeclaracaoDTO> handle(GetPedidoDeclaracoesQuery query) {

    LOGGER.debug("GetPedidoDeclaracoesQuery: {}", query);

    WrapperListaPedidoDeclaracaoDTO response = pedidoDeclaracaoReadService.findAll(query);

    return ResponseEntity.ok(response);
  }

}