package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.WrapperListProcessoEtapaDTO;
import cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaProcessosEtapaQueryHandler implements QueryHandler<GetListaProcessosEtapaQuery, ResponseEntity<WrapperListProcessoEtapaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaProcessosEtapaQueryHandler.class);

  private final MissaoProcessoServiceRead missaoProcessoServiceRead;

  public GetListaProcessosEtapaQueryHandler(MissaoProcessoServiceRead missaoProcessoServiceRead) {
    this.missaoProcessoServiceRead = missaoProcessoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListProcessoEtapaDTO> handle(GetListaProcessosEtapaQuery query) {

    LOGGER.debug("GetListaProcessosEtapaQuery: {}", query);

    return missaoProcessoServiceRead.listarPorEtapa(query);
  }

}
