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
public class GetCarreiraProcessandoAtivaQueryHandler implements QueryHandler<GetCarreiraProcessandoAtivaQuery, ResponseEntity<CarreiraResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraProcessandoAtivaQueryHandler.class);

  private final CarreiraReadService carreiraReadService;

  public GetCarreiraProcessandoAtivaQueryHandler(CarreiraReadService carreiraReadService) {

    this.carreiraReadService = carreiraReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<CarreiraResponseDTO> handle(GetCarreiraProcessandoAtivaQuery query) {

    LOGGER.debug("GetCarreiraProcessandoAtivaQuery: {}", query);

     var data = carreiraReadService.carreiraProcessandoAtiva(query.getIdFuncionario());

     return ResponseEntity.ok(data);
  }

}
