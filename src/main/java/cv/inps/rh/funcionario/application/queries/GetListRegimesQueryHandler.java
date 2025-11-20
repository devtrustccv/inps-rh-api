package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.service.RegimeReadService;

import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;

import java.util.List;

@Component
public class GetListRegimesQueryHandler implements QueryHandler<GetListRegimesQuery, ResponseEntity<WrapperRegimeListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRegimesQueryHandler.class);


  private final RegimeReadService regimeReadService;

  public GetListRegimesQueryHandler( RegimeReadService regimeReadService) {

    this.regimeReadService = regimeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperRegimeListDTO> handle(GetListRegimesQuery query) {

    LOGGER.info("Handling GetListRegimesQuery: {}", query);

    return ResponseEntity.ok(regimeReadService.listRegime(query));
  }

}
