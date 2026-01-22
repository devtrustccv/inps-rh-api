package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;

@Component
public class GetPedidoFeriaQueryHandler implements QueryHandler<GetPedidoFeriaQuery, ResponseEntity<PedidoFeriaAlterarReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoFeriaQueryHandler.class);


  public GetPedidoFeriaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<PedidoFeriaAlterarReqDTO> handle(GetPedidoFeriaQuery query) {

    LOGGER.debug("GetPedidoFeriaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}