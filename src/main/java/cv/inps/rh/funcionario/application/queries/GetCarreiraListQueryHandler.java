package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.service.CarreiraReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetCarreiraListQueryHandler implements QueryHandler<GetCarreiraListQuery, ResponseEntity<WrapperCarreiraListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraListQueryHandler.class);

  private final CarreiraReadService carreiraReadService;

  public GetCarreiraListQueryHandler(CarreiraReadService carreiraReadService) {

    this.carreiraReadService = carreiraReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperCarreiraListDTO> handle(GetCarreiraListQuery query) {


    LOGGER.info("Handling GetCarreiraListQuery: {}", query);

    return ResponseEntity.ok(carreiraReadService.list(query));

  }

}
