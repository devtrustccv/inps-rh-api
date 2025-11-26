package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.DadosBancariosReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;

import java.util.List;

@Component
public class GetDadosBancariosQueryHandler implements QueryHandler<GetDadosBancariosQuery, ResponseEntity<List<DadosBancariosRespDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosBancariosQueryHandler.class);

  private final DadosBancariosReadService dadosBancariosReadService;

  public GetDadosBancariosQueryHandler(DadosBancariosReadService dadosBancariosReadService) {

    this.dadosBancariosReadService = dadosBancariosReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<DadosBancariosRespDTO>> handle(GetDadosBancariosQuery query) {

    return ResponseEntity.ok(dadosBancariosReadService.getDadosBancarios(query));
  }

}
