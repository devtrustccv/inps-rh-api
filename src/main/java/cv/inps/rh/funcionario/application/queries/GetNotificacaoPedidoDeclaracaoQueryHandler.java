package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.NotificacaoResponseDTO;

@Component
public class GetNotificacaoPedidoDeclaracaoQueryHandler implements QueryHandler<GetNotificacaoPedidoDeclaracaoQuery, ResponseEntity<NotificacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacaoPedidoDeclaracaoQueryHandler.class);


  public GetNotificacaoPedidoDeclaracaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<NotificacaoResponseDTO> handle(GetNotificacaoPedidoDeclaracaoQuery query) {

    LOGGER.debug("GetNotificacaoPedidoDeclaracaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}