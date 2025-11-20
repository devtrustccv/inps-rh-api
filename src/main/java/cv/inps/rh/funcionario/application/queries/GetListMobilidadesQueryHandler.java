package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.MobilidadeListDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeReadService;
import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;

import java.util.List;

@Component
public class GetListMobilidadesQueryHandler implements QueryHandler<GetListMobilidadesQuery, ResponseEntity<WrapperListMobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListMobilidadesQueryHandler.class);

  private final MobilidadeRepository mobilidadeRepository;
  private final MobilidadeMapper mobilidadeMapper;

  private final MobilidadeReadService mobilidadeReadService;

  public GetListMobilidadesQueryHandler(MobilidadeRepository mobilidadeRepository, MobilidadeMapper mobilidadeMapper, MobilidadeReadService mobilidadeReadService) {

    this.mobilidadeRepository = mobilidadeRepository;
    this.mobilidadeMapper = mobilidadeMapper;
    this.mobilidadeReadService = mobilidadeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListMobilidadeDTO> handle(GetListMobilidadesQuery query) {
     LOGGER.info("Handling GetListMobilidadesQuery: {}", query);

     return ResponseEntity.ok(mobilidadeReadService.getListMobilidade(query));


  }

}
