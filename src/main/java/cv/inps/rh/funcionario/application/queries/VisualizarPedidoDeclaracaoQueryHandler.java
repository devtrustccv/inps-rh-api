package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class VisualizarPedidoDeclaracaoQueryHandler implements QueryHandler<VisualizarPedidoDeclaracaoQuery, ResponseEntity<String>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(VisualizarPedidoDeclaracaoQueryHandler.class);


  public VisualizarPedidoDeclaracaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<String> handle(VisualizarPedidoDeclaracaoQuery query) {

    LOGGER.debug("VisualizarPedidoDeclaracaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}