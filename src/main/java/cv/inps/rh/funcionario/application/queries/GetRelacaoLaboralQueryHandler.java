package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;

@Component
public class GetRelacaoLaboralQueryHandler implements QueryHandler<GetRelacaoLaboralQuery, ResponseEntity<WrapperHistLaboralResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRelacaoLaboralQueryHandler.class);


  public GetRelacaoLaboralQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperHistLaboralResponseDTO> handle(GetRelacaoLaboralQuery query) {

    LOGGER.debug("GetRelacaoLaboralQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}