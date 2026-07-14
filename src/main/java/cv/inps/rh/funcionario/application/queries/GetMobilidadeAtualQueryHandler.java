package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMobilidadeAtualQueryHandler implements QueryHandler<GetMobilidadeAtualQuery, ResponseEntity<MobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMobilidadeAtualQueryHandler.class);

  private final MobilidadeReadService mobilidadeReadService;

  public GetMobilidadeAtualQueryHandler(MobilidadeReadService mobilidadeReadService) {

    this.mobilidadeReadService = mobilidadeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<MobilidadeDTO> handle(GetMobilidadeAtualQuery query) {
     LOGGER.info("Handling GetMobilidadeAtualQuery: {}", query);
     return ResponseEntity.ok(mobilidadeReadService.getMobilidadeAtual(query));
  }

}
