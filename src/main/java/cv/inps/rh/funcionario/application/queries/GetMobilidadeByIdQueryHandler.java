package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeReadService;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMobilidadeByIdQueryHandler implements QueryHandler<GetMobilidadeByIdQuery, ResponseEntity<MobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMobilidadeByIdQueryHandler.class);

  private final MobilidadeReadService mobilidadeReadService;

  public GetMobilidadeByIdQueryHandler(MobilidadeReadService mobilidadeReadService) {

    this.mobilidadeReadService = mobilidadeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<MobilidadeDTO> handle(GetMobilidadeByIdQuery query) {
     LOGGER.info("Handling GetMobilidadeByIdQuery: {}", query);
     return ResponseEntity.ok(mobilidadeReadService.getMobilidade(query));
  }

}
