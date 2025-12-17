package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.service.ContratoReadService;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListContratosQueryHandler implements QueryHandler<GetListContratosQuery, ResponseEntity<WrapperListContratoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListContratosQueryHandler.class);



  private final ContratoReadService contratoReadService;

  public GetListContratosQueryHandler( ContratoMapper contratoMapper, ContratoReadService contratoReadService) {

    this.contratoReadService = contratoReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListContratoDTO> handle(GetListContratosQuery query) {


     LOGGER.info("Handling GetListContratosQuery: {}", query);

     return ResponseEntity.ok(contratoReadService.listaContratos(query));

   }

}
