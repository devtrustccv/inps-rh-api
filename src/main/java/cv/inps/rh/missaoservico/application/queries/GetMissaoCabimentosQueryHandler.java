package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentosResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMissaoCabimentosQueryHandler implements QueryHandler<GetMissaoCabimentosQuery, ResponseEntity<MissaoCabimentosResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoCabimentosQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetMissaoCabimentosQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoCabimentosResponseDTO> handle(GetMissaoCabimentosQuery query) {

    LOGGER.debug("GetMissaoCabimentosQuery: {}", query);

    return missaoProcessoServiceRead.getCabimentosDaMissao(query);
  }

}
