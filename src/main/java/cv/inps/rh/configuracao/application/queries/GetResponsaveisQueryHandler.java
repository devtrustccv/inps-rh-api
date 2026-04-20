package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.WrapperListResponsaveisDTO;
import cv.inps.rh.configuracao.application.services.ResponsavelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetResponsaveisQueryHandler implements QueryHandler<GetResponsaveisQuery, ResponseEntity<WrapperListResponsaveisDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetResponsaveisQueryHandler.class);

  private final ResponsavelService responsavelService;

  public GetResponsaveisQueryHandler(ResponsavelService responsavelService) {

    this.responsavelService = responsavelService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListResponsaveisDTO> handle(GetResponsaveisQuery query) {

    LOGGER.debug("GetResponsaveisQuery: {}", query);

    return ResponseEntity.ok(responsavelService.getResponsaveis(query));
  }

}
