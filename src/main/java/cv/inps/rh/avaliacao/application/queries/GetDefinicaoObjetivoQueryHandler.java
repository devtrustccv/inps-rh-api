package cv.inps.rh.avaliacao.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;

@Component
public class GetDefinicaoObjetivoQueryHandler implements QueryHandler<GetDefinicaoObjetivoQuery, ResponseEntity<DefinicaoObjectivoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDefinicaoObjetivoQueryHandler.class);


  public GetDefinicaoObjetivoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DefinicaoObjectivoDTO> handle(GetDefinicaoObjetivoQuery query) {

    LOGGER.debug("GetDefinicaoObjetivoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}