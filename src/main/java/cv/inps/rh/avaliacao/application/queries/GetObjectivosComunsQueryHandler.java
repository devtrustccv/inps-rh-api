package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.ObjectivosComunsDTO;
import cv.inps.rh.avaliacao.application.services.ObjectivosComunsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetObjectivosComunsQueryHandler implements QueryHandler<GetObjectivosComunsQuery, ResponseEntity<ObjectivosComunsDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetObjectivosComunsQueryHandler.class);

  private final ObjectivosComunsService objectivosComunsService;

  public GetObjectivosComunsQueryHandler(ObjectivosComunsService objectivosComunsService) {
    this.objectivosComunsService = objectivosComunsService;

  }

   @IgrpQueryHandler
  public ResponseEntity<ObjectivosComunsDTO> handle(GetObjectivosComunsQuery query) {

    LOGGER.debug("GetObjectivosComunsQuery: {}", query);

    return ResponseEntity.ok(objectivosComunsService.obter(query.getAno(), query.getPeriodicidade()));
  }

}
