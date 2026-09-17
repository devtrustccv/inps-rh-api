package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetRequisicaoPdfQueryHandler implements QueryHandler<GetRequisicaoPdfQuery, ResponseEntity<byte[]>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRequisicaoPdfQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetRequisicaoPdfQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(GetRequisicaoPdfQuery query) {

    LOGGER.debug("GetRequisicaoPdfQuery: {}", query);

    return missaoProcessoServiceRead.getRequisicaoPdf(query);
  }

}
