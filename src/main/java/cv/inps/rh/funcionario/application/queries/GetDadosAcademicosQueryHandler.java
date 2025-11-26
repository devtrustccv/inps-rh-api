package cv.inps.rh.funcionario.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;

@Component
public class GetDadosAcademicosQueryHandler implements QueryHandler<GetDadosAcademicosQuery, ResponseEntity<DadosAcademicosProfResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosAcademicosQueryHandler.class);


  public GetDadosAcademicosQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<DadosAcademicosProfResponseDTO> handle(GetDadosAcademicosQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}