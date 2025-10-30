package cv.inps.rh.parametrizacao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;

@Component
public class GetSituacaoLaboralAtivoQueryHandler implements QueryHandler<GetSituacaoLaboralAtivoQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSituacaoLaboralAtivoQueryHandler.class);


  public GetSituacaoLaboralAtivoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetSituacaoLaboralAtivoQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}