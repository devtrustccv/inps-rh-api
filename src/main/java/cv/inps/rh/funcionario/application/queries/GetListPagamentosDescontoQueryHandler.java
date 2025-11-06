package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;

@Component
public class GetListPagamentosDescontoQueryHandler implements QueryHandler<GetListPagamentosDescontoQuery, ResponseEntity<WrapperListPagamentosDescontoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListPagamentosDescontoQueryHandler.class);


  public GetListPagamentosDescontoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListPagamentosDescontoDTO> handle(GetListPagamentosDescontoQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}