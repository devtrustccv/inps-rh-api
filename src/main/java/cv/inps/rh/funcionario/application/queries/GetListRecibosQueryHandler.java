package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListReciboDTO;
import cv.inps.rh.funcionario.application.service.recibo.ReciboReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListRecibosQueryHandler implements QueryHandler<GetListRecibosQuery, ResponseEntity<WrapperListReciboDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRecibosQueryHandler.class);

  private final ReciboReadService reciboReadService;

  public GetListRecibosQueryHandler(ReciboReadService reciboReadService) {
    this.reciboReadService = reciboReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListReciboDTO> handle(GetListRecibosQuery query) {

    LOGGER.info("Handling GetListRecibosQuery: {}", query);

    return ResponseEntity.ok(reciboReadService.getListRecibos(query));

  }

}
