package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GetUrlTemporarioQueryHandler implements QueryHandler<GetUrlTemporarioQuery, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetUrlTemporarioQueryHandler.class);


  public GetUrlTemporarioQueryHandler() {

  }

  @IgrpQueryHandler
  public ResponseEntity<String> handle(GetUrlTemporarioQuery query) {

    LOGGER.debug("GetUrlTemporarioQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}
