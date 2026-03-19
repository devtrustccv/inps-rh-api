package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoReqResponseDTO;

@Component
public class GetSubmissaoServicoEmissaoRequisicaoQueryHandler implements QueryHandler<GetSubmissaoServicoEmissaoRequisicaoQuery, ResponseEntity<MissaoEmissaoReqResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubmissaoServicoEmissaoRequisicaoQueryHandler.class);


  public GetSubmissaoServicoEmissaoRequisicaoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoEmissaoReqResponseDTO> handle(GetSubmissaoServicoEmissaoRequisicaoQuery query) {

    LOGGER.debug("GetSubmissaoServicoEmissaoRequisicaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}