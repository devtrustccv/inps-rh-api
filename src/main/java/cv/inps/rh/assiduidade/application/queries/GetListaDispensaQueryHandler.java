package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.DispensaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;

@Component
public class GetListaDispensaQueryHandler implements QueryHandler<GetListaDispensaQuery, ResponseEntity<WrapperListaDispensaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaDispensaQueryHandler.class);

  private final DispensaReadService dispensaReadService;

  public GetListaDispensaQueryHandler(DispensaReadService dispensaReadService) {

    this.dispensaReadService = dispensaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaDispensaDTO> handle(GetListaDispensaQuery query) {

    LOGGER.debug("GetListaDispensaQuery: {}", query);


    return ResponseEntity.ok(dispensaReadService.getListaDispensa(query));
  }

}
