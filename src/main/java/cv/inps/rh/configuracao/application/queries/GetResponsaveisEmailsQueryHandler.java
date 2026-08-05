package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.ResponsavelEmailDTO;
import cv.inps.rh.configuracao.application.services.ResponsavelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetResponsaveisEmailsQueryHandler
    implements QueryHandler<GetResponsaveisEmailsQuery, ResponseEntity<List<ResponsavelEmailDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetResponsaveisEmailsQueryHandler.class);

  private final ResponsavelService responsavelService;

  public GetResponsaveisEmailsQueryHandler(ResponsavelService responsavelService) {
    this.responsavelService = responsavelService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ResponsavelEmailDTO>> handle(GetResponsaveisEmailsQuery query) {

    LOGGER.debug("GetResponsaveisEmailsQuery: {}", query);

    return ResponseEntity.ok(responsavelService.getResponsaveisEmails(query));
  }
}
