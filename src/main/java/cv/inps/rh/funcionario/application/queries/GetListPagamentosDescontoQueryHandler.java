package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;
import cv.inps.rh.funcionario.application.service.PagamentosDescontoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListPagamentosDescontoQueryHandler implements QueryHandler<GetListPagamentosDescontoQuery, ResponseEntity<WrapperListPagamentosDescontoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListPagamentosDescontoQueryHandler.class);

  private final PagamentosDescontoReadService pagamentosDescontoReadService;

  public GetListPagamentosDescontoQueryHandler(PagamentosDescontoReadService pagamentosDescontoReadService) {

    this.pagamentosDescontoReadService = pagamentosDescontoReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListPagamentosDescontoDTO> handle(GetListPagamentosDescontoQuery query) {
     LOGGER.info("Handling GetListPagamentosDescontoQuery: {}", query);

      return ResponseEntity.ok(pagamentosDescontoReadService.getListPagamentosDesconto(query));

  }

}
