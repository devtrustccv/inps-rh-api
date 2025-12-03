package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamContratoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetParamContratosAtivosQueryHandler implements QueryHandler<GetParamContratosAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetParamContratosAtivosQueryHandler.class);

  private final ParamContratoRepository paramContratoRepository;
  private final ParamContratoMapper paramContratoMapper;

  public GetParamContratosAtivosQueryHandler(ParamContratoRepository paramContratoRepository, ParamContratoMapper paramContratoMapper) {

    this.paramContratoRepository = paramContratoRepository;
    this.paramContratoMapper = paramContratoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamContratosAtivosQuery query) {
     var paramContratos =  paramContratoRepository.findAllActive();
     List<ParametrizacaoDTO> parametrizacoes = paramContratos.stream()
         .map(paramContratoMapper::toParametrizacaoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
