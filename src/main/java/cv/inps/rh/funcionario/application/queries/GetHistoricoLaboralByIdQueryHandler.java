package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;

@Component
public class GetHistoricoLaboralByIdQueryHandler implements QueryHandler<GetHistoricoLaboralByIdQuery, ResponseEntity<ValidarNovoHistoricoLaboralDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHistoricoLaboralByIdQueryHandler.class);


  public GetHistoricoLaboralByIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> handle(GetHistoricoLaboralByIdQuery query) {

    LOGGER.debug("GetHistoricoLaboralByIdQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}