package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.DispensaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;

@Component
public class GetDispensaByPedidoIdQueryHandler implements QueryHandler<GetDispensaByPedidoIdQuery, ResponseEntity<DispensaReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDispensaByPedidoIdQueryHandler.class);

  private final DispensaReadService dispensaReadService;

  public GetDispensaByPedidoIdQueryHandler(DispensaReadService dispensaReadService) {

    this.dispensaReadService = dispensaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<DispensaReqDTO> handle(GetDispensaByPedidoIdQuery query) {

    LOGGER.debug("GetDispensaByPedidoIdQuery: {}", query);


    return ResponseEntity.ok(dispensaReadService.getDispensaByPedidoId(query));
  }

}
