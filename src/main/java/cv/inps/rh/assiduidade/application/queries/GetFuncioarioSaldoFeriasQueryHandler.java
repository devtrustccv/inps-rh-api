package cv.inps.rh.assiduidade.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetFuncioarioSaldoFeriasQueryHandler implements QueryHandler<GetFuncioarioSaldoFeriasQuery, ResponseEntity<Map<String, ?>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncioarioSaldoFeriasQueryHandler.class);


  public GetFuncioarioSaldoFeriasQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<Map<String, ?>> handle(GetFuncioarioSaldoFeriasQuery query) {

    LOGGER.debug("GetFuncioarioSaldoFeriasQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}