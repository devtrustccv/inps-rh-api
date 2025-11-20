package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.service.ValidacoesReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetValicoesUtilizadoresQueryHandler implements QueryHandler<GetValicoesUtilizadoresQuery, ResponseEntity<WrapperListaValidacoesDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetValicoesUtilizadoresQueryHandler.class);

  private final ValidacoesReadService validacoesReadService;

  public GetValicoesUtilizadoresQueryHandler(ValidacoesReadService validacoesReadService) {

    this.validacoesReadService = validacoesReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaValidacoesDTO> handle(GetValicoesUtilizadoresQuery query) {
     LOGGER.info("Handling GetValicoesUtilizadoresQuery: {}", query);

     return ResponseEntity.ok(validacoesReadService.listaValidacoes(query));
  }

}
