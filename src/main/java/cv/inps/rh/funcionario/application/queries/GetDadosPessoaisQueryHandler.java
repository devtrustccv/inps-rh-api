package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;

@Component
public class GetDadosPessoaisQueryHandler implements QueryHandler<GetDadosPessoaisQuery, ResponseEntity<DadosPessoaisRespDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosPessoaisQueryHandler.class);


  public GetDadosPessoaisQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DadosPessoaisRespDTO> handle(GetDadosPessoaisQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}