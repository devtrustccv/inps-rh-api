package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;

@Component
public class GetRelacaoLaboralByFunIdQueryHandler implements QueryHandler<GetRelacaoLaboralByFunIdQuery, ResponseEntity<RelacaoLaboralDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralByFunIdQueryHandler.class);


  public GetRelacaoLaboralByFunIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<RelacaoLaboralDTO> handle(GetRelacaoLaboralByFunIdQuery query) {

    LOGGER.debug("GetRelacaoLaboralByFunIdQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}