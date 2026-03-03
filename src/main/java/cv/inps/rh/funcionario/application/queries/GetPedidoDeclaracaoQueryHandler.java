package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;

@Component
public class GetPedidoDeclaracaoQueryHandler implements QueryHandler<GetPedidoDeclaracaoQuery, ResponseEntity<PedidoDeclaracaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoDeclaracaoQueryHandler.class);


  public GetPedidoDeclaracaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<PedidoDeclaracaoResponseDTO> handle(GetPedidoDeclaracaoQuery query) {

    LOGGER.debug("GetPedidoDeclaracaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}