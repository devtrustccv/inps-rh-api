package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import cv.inps.rh.assiduidade.application.services.DispensaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDispensaQueryHandler implements QueryHandler<GetDispensaQuery, ResponseEntity<DispensaReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDispensaQueryHandler.class);

  private final DispensaReadService dispensaReadService;

  public GetDispensaQueryHandler(DispensaReadService dispensaReadService) {

    this.dispensaReadService = dispensaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<DispensaReqDTO> handle(GetDispensaQuery query) {

    LOGGER.debug("GetDispensaQuery: {}", query);

    return ResponseEntity.ok(dispensaReadService.getDispensa(query));
  }

}
