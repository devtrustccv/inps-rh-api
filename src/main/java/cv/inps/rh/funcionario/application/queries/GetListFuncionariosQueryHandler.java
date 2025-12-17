package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.service.FuncionarioReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListFuncionariosQueryHandler implements QueryHandler<GetListFuncionariosQuery, ResponseEntity<WrapperListaFuncionarioDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListFuncionariosQueryHandler.class);

  private final FuncionarioReadService funcionarioReadService;

  public GetListFuncionariosQueryHandler(FuncionarioReadService funcionarioReadService) {


    this.funcionarioReadService = funcionarioReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListaFuncionarioDTO> handle(GetListFuncionariosQuery query) {
    LOGGER.info("Handling GetListFuncionariosQuery: {}", query);

    return ResponseEntity.ok(funcionarioReadService.getListFuncionarios(query));

  }
}
