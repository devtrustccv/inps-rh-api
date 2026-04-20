package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.carreira.CarreiraReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.CarreiraResponseDTO;

@Component
public class GetCarreiraAtualQueryHandler implements QueryHandler<GetCarreiraAtualQuery, ResponseEntity<CarreiraResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraAtualQueryHandler.class);

  private final CarreiraReadService carreiraReadService;

  public GetCarreiraAtualQueryHandler(CarreiraReadService carreiraReadService) {

    this.carreiraReadService = carreiraReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<CarreiraResponseDTO> handle(GetCarreiraAtualQuery query) {

    LOGGER.debug("GetCarreiraAtualQuery: {}", query);

     var data = carreiraReadService.getCarreiraAtualByUuidFuncionario(query.getUuidFuncionario());

     return ResponseEntity.ok(data);
  }

}
