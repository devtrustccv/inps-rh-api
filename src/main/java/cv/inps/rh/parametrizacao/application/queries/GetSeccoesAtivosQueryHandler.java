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
public class GetSeccoesAtivosQueryHandler implements QueryHandler<GetSeccoesAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSeccoesAtivosQueryHandler.class);


  public GetSeccoesAtivosQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetSeccoesAtivosQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}