package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamSituacaoLaboralRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetParamSituacaoLaboralAtivoQueryHandler implements QueryHandler<GetParamSituacaoLaboralAtivoQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetParamSituacaoLaboralAtivoQueryHandler.class);

  private final ParamSituacaoLaboralRepository paramSituacaoLaboralRepository;
  private final ParamSitLaboralMapper paramSitLaboralMapper;

  public GetParamSituacaoLaboralAtivoQueryHandler(ParamSituacaoLaboralRepository paramSituacaoLaboralRepository, ParamSitLaboralMapper paramSitLaboralMapper) {

    this.paramSituacaoLaboralRepository = paramSituacaoLaboralRepository;
    this.paramSitLaboralMapper = paramSitLaboralMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamSituacaoLaboralAtivoQuery query) {
      var paramSituacoes =  paramSituacaoLaboralRepository.findAllActive();
      List<ParametrizacaoDTO> parametrizacoes = paramSituacoes.stream()
          .map(paramSitLaboralMapper::toParametrizacaoDto)
          .toList();
      return ResponseEntity.ok(parametrizacoes);
  }

}
