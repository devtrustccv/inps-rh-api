package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamCategoriaRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCategoriaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetCategoriasAtivosQueryHandler implements QueryHandler<GetCategoriasAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCategoriasAtivosQueryHandler.class);

  private final ParamCategoriaRepository paramCategoriaRepository;
  private final ParamCategoriaMapper paramCategoriaMapper;

  public GetCategoriasAtivosQueryHandler(ParamCategoriaRepository paramCategoriaRepository, ParamCategoriaMapper paramCategoriaMapper) {

    this.paramCategoriaRepository = paramCategoriaRepository;
    this.paramCategoriaMapper = paramCategoriaMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetCategoriasAtivosQuery query) {
     var paramCategorias =  paramCategoriaRepository.findAllActive();

     List<ParametrizacaoDTO> parametrizacoesCategorias = paramCategorias.stream()
         .map(paramCategoriaMapper::toParametrizacaoDto)
         .toList();

     return ResponseEntity.ok(parametrizacoesCategorias);
  }

}
