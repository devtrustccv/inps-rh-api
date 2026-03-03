package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListaPedidoDeclaracaoDTO;

@Component
public class GetPedidoDeclaracoesQueryHandler implements QueryHandler<GetPedidoDeclaracoesQuery, ResponseEntity<WrapperListaPedidoDeclaracaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoDeclaracoesQueryHandler.class);


  public GetPedidoDeclaracoesQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaPedidoDeclaracaoDTO> handle(GetPedidoDeclaracoesQuery query) {

    LOGGER.debug("GetPedidoDeclaracoesQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}