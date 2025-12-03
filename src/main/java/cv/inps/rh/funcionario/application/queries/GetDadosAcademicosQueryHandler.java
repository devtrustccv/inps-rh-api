package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import cv.inps.rh.funcionario.application.service.DadosAcademicosReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDadosAcademicosQueryHandler implements QueryHandler<GetDadosAcademicosQuery, ResponseEntity<DadosAcademicosProfResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosAcademicosQueryHandler.class);

  private final DadosAcademicosReadService dadosAcademicosReadService;

  public GetDadosAcademicosQueryHandler(DadosAcademicosReadService dadosAcademicosReadService) {

    this.dadosAcademicosReadService = dadosAcademicosReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<DadosAcademicosProfResponseDTO> handle(GetDadosAcademicosQuery query) {

    return ResponseEntity.ok(dadosAcademicosReadService.getDadosAcademicos(query));
  }

}
