package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.ResponsaveisDirecaoResponseDTO;
import cv.inps.rh.configuracao.application.services.ResponsavelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetResponsaveisDirecaoQueryHandler implements QueryHandler<GetResponsaveisDirecaoQuery, ResponseEntity<ResponsaveisDirecaoResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetResponsaveisDirecaoQueryHandler.class);

  private final ResponsavelService responsavelService;

  public GetResponsaveisDirecaoQueryHandler(ResponsavelService responsavelService) {
    this.responsavelService = responsavelService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ResponsaveisDirecaoResponseDTO> handle(GetResponsaveisDirecaoQuery query) {

    LOGGER.debug("GetResponsaveisDirecaoQuery: {}", query);

    var data = responsavelService.getResponsavelData(Long.valueOf(query.getInstitutoId()), query.getSeccaoId());

    return ResponseEntity.ok(data);
  }

}
