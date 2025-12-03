package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamCargoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCargoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetCargosAtivosQueryHandler implements QueryHandler<GetCargosAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCargosAtivosQueryHandler.class);

   private final ParamCargoRepository paramCargoRepository;
   private final ParamCargoMapper paramCargoMapper;

  public GetCargosAtivosQueryHandler(ParamCargoRepository paramCargoRepository, ParamCargoMapper paramCargoMapper) {

    this.paramCargoRepository = paramCargoRepository;
    this.paramCargoMapper = paramCargoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetCargosAtivosQuery query) {
    LOGGER.info("Handling query {}", query);
    var paramCargos =  paramCargoRepository.findAllActive();

    List<ParametrizacaoDTO> parametrizacoes = paramCargos.stream()
        .map(paramCargoMapper::toParametrizacaoDto)
        .toList();

    return ResponseEntity.ok(parametrizacoes);
  }

}
