package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.CarreiraResponseDTO;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetCarreiraByIdQueryHandler implements QueryHandler<GetCarreiraByIdQuery, ResponseEntity<CarreiraResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraByIdQueryHandler.class);

  private final CarreiraReadService carreiraReadService;

  public GetCarreiraByIdQueryHandler(CarreiraReadService carreiraReadService) {
    this.carreiraReadService = carreiraReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<CarreiraResponseDTO> handle(GetCarreiraByIdQuery query) {

    var data = carreiraReadService.getCarreiraById(query.getCarreiraId());

    return ResponseEntity.ok(data);
  }

}
