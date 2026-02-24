package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.transversal.application.dto.DossierColaboradorListDTO;
import cv.inps.rh.transversal.application.service.RelatorioDossierService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class RelatorioDossierColaboradorQueryHandler implements QueryHandler<RelatorioDossierColaboradorQuery, ResponseEntity<DossierColaboradorListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(RelatorioDossierColaboradorQueryHandler.class);
  private final RelatorioDossierService relatorioDossierService;

   @IgrpQueryHandler
  public ResponseEntity<DossierColaboradorListDTO> handle(RelatorioDossierColaboradorQuery query) {

    LOGGER.debug("RelatorioDossierColaboradorQuery: {}", query);


    DossierColaboradorListDTO responseDTO = relatorioDossierService.get(query);

    return ResponseEntity.ok(responseDTO);
  }

}
