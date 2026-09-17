package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.ProcessoAprovacaoRhResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessoAprovacaoRhQueryHandler implements QueryHandler<GetProcessoAprovacaoRhQuery, ResponseEntity<ProcessoAprovacaoRhResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessoAprovacaoRhQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetProcessoAprovacaoRhQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessoAprovacaoRhResponseDTO> handle(GetProcessoAprovacaoRhQuery query) {

    LOGGER.debug("GetProcessoAprovacaoRhQuery: {}", query);

    return missaoProcessoServiceRead.getAprovacaoRh(query);
  }

}
