package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListRenumeracoesQueryHandler implements QueryHandler<GetListRenumeracoesQuery, ResponseEntity<WrapperListRenumeracaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRenumeracoesQueryHandler.class);


  public GetListRenumeracoesQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListRenumeracaoDTO> handle(GetListRenumeracoesQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}
