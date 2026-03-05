package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.funcionario.application.dto.AlertaDTO;

@Component
public class GetNotificacoesGeradasByAlertaQueryHandler implements QueryHandler<GetNotificacoesGeradasByAlertaQuery, ResponseEntity<List<AlertaDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacoesGeradasByAlertaQueryHandler.class);


  public GetNotificacoesGeradasByAlertaQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<List<AlertaDTO>> handle(GetNotificacoesGeradasByAlertaQuery query) {

    LOGGER.debug("GetNotificacoesGeradasByAlertaQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}