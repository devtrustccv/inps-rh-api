package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.services.JustificarFaltaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetJustificacaoFaltaQueryHandler implements QueryHandler<GetJustificacaoFaltaQuery, ResponseEntity<JustificarFaltaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetJustificacaoFaltaQueryHandler.class);

  private final JustificarFaltaReadService justificarFaltaReadService;

  public GetJustificacaoFaltaQueryHandler(JustificarFaltaReadService justificarFaltaReadService) {

    this.justificarFaltaReadService = justificarFaltaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<JustificarFaltaDTO> handle(GetJustificacaoFaltaQuery query) {

    LOGGER.debug("GetFaltaJustificadaQuery: {}", query);

    return ResponseEntity.ok(justificarFaltaReadService.getFaltaJustificadaResumo(query));
  }

}
