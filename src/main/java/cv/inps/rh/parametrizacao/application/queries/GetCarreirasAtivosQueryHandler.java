package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.parametrizacao.domain.repository.ParamCarreiraRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCarreiraMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;

@Component
public class GetCarreirasAtivosQueryHandler implements QueryHandler<GetCarreirasAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreirasAtivosQueryHandler.class);

  private final ParamCarreiraMapper paramCarreiraMapper;
  private final ParamCarreiraRepository paramCarreiraRepository;

  public GetCarreirasAtivosQueryHandler(ParamCarreiraMapper paramCarreiraMapper, ParamCarreiraRepository paramCarreiraRepository) {

    this.paramCarreiraMapper = paramCarreiraMapper;
    this.paramCarreiraRepository = paramCarreiraRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetCarreirasAtivosQuery query) {

     var paramCarreiras =  paramCarreiraRepository.findAllActive();
     List<ParametrizacaoDTO> parametrizacoes = paramCarreiras.stream()
         .map(paramCarreiraMapper::toParametrizacaoDto)
         .toList();

     return ResponseEntity.ok(parametrizacoes);
  }

}
