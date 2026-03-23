package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoReqResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;

@Component
public class GetSubmissaoServicoEmissaoRequisicaoQueryHandler implements QueryHandler<GetSubmissaoServicoEmissaoRequisicaoQuery, ResponseEntity<MissaoEmissaoReqResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubmissaoServicoEmissaoRequisicaoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetSubmissaoServicoEmissaoRequisicaoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoEmissaoReqResponseDTO> handle(GetSubmissaoServicoEmissaoRequisicaoQuery query) {

    LOGGER.debug("GetSubmissaoServicoEmissaoRequisicaoQuery: {}", query);
    return missaoServicoServiceRead.getEmissaoRequisicao(query);
  }

}
