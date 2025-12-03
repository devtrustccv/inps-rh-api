package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListMobilidadesQueryHandler implements QueryHandler<GetListMobilidadesQuery, ResponseEntity<WrapperListMobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListMobilidadesQueryHandler.class);

  private final MobilidadeReadService mobilidadeReadService;

  public GetListMobilidadesQueryHandler(MobilidadeReadService mobilidadeReadService) {

    this.mobilidadeReadService = mobilidadeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListMobilidadeDTO> handle(GetListMobilidadesQuery query) {
     LOGGER.info("Handling GetListMobilidadesQuery: {}", query);

     return ResponseEntity.ok(mobilidadeReadService.getListMobilidade(query));


  }

}
