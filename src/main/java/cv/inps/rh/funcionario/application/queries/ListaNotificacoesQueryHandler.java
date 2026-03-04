package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.shared.application.dto.WrapperListaNotificacoesDTO;

@Component
public class ListaNotificacoesQueryHandler implements QueryHandler<ListaNotificacoesQuery, ResponseEntity<WrapperListaNotificacoesDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ListaNotificacoesQueryHandler.class);


  public ListaNotificacoesQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaNotificacoesDTO> handle(ListaNotificacoesQuery query) {

    LOGGER.debug("ListaNotificacoesQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}