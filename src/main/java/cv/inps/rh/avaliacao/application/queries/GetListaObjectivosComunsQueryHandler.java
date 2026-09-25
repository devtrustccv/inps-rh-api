package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.WrapperListaObjectivosComunsDTO;
import cv.inps.rh.avaliacao.application.services.ObjectivosComunsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaObjectivosComunsQueryHandler implements QueryHandler<GetListaObjectivosComunsQuery, ResponseEntity<WrapperListaObjectivosComunsDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaObjectivosComunsQueryHandler.class);

  private final ObjectivosComunsService objectivosComunsService;

  public GetListaObjectivosComunsQueryHandler(ObjectivosComunsService objectivosComunsService) {
    this.objectivosComunsService = objectivosComunsService;

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaObjectivosComunsDTO> handle(GetListaObjectivosComunsQuery query) {

    LOGGER.debug("GetListaObjectivosComunsQuery: {}", query);

    return ResponseEntity.ok(objectivosComunsService.listar(query.getAno(), query.getPageNumber(), query.getPageSize()));
  }

}
