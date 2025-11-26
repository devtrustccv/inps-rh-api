package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;

@Component
public class GetDadosBancariosQueryHandler implements QueryHandler<GetDadosBancariosQuery, ResponseEntity<DadosBancariosRespDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosBancariosQueryHandler.class);


  public GetDadosBancariosQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DadosBancariosRespDTO> handle(GetDadosBancariosQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}