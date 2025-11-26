package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.DadosPessoaisReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;

@Component
public class GetDadosPessoaisQueryHandler implements QueryHandler<GetDadosPessoaisQuery, ResponseEntity<DadosPessoaisRespDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosPessoaisQueryHandler.class);

  private final DadosPessoaisReadService dadosPessoaisReadService;

  public GetDadosPessoaisQueryHandler(DadosPessoaisReadService dadosPessoaisReadService) {

    this.dadosPessoaisReadService = dadosPessoaisReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<DadosPessoaisRespDTO> handle(GetDadosPessoaisQuery query) {

    return ResponseEntity.ok(dadosPessoaisReadService.getDadosPessoais(query));
  }

}
