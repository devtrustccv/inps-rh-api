package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);


  public GetContratoByIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}