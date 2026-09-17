package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.ProcessoValidacaoUgalResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessoValidacaoUgalQueryHandler implements QueryHandler<GetProcessoValidacaoUgalQuery, ResponseEntity<ProcessoValidacaoUgalResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessoValidacaoUgalQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetProcessoValidacaoUgalQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessoValidacaoUgalResponseDTO> handle(GetProcessoValidacaoUgalQuery query) {

    LOGGER.debug("GetProcessoValidacaoUgalQuery: {}", query);

    return missaoProcessoServiceRead.getValidacaoUgal(query);
  }

}
