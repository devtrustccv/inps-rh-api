package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;
import cv.inps.rh.funcionario.application.service.declaracao.PedidoDeclaracaoReadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetPedidoDeclaracaoQueryHandler implements QueryHandler<GetPedidoDeclaracaoQuery, ResponseEntity<PedidoDeclaracaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoDeclaracaoQueryHandler.class);
  private final PedidoDeclaracaoReadService pedidoDeclaracaoReadService;

   @IgrpQueryHandler
  public ResponseEntity<PedidoDeclaracaoResponseDTO> handle(GetPedidoDeclaracaoQuery query) {

    LOGGER.debug("GetPedidoDeclaracaoQuery: {}", query);

    PedidoDeclaracaoResponseDTO response = pedidoDeclaracaoReadService.findById(query.getId());

    return ResponseEntity.ok(response);
  }

}
