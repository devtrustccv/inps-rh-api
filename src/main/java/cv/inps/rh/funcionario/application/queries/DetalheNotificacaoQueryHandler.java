package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;

@Component
public class DetalheNotificacaoQueryHandler implements QueryHandler<DetalheNotificacaoQuery, ResponseEntity<NotificacaoInfoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(DetalheNotificacaoQueryHandler.class);


  public DetalheNotificacaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<NotificacaoInfoDTO> handle(DetalheNotificacaoQuery query) {

    LOGGER.debug("DetalheNotificacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}