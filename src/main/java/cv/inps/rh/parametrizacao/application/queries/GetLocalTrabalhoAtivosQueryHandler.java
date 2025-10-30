package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.parametrizacao.domain.repository.ParamLocalTrabalhoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamLocalTrabMapper;
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
public class GetLocalTrabalhoAtivosQueryHandler implements QueryHandler<GetLocalTrabalhoAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetLocalTrabalhoAtivosQueryHandler.class);

   private final ParamLocalTrabalhoRepository paramLocalTrabalhoRepository;
   private final ParamLocalTrabMapper paramLocalTrabMapper;

  public GetLocalTrabalhoAtivosQueryHandler(ParamLocalTrabalhoRepository paramLocalTrabalhoRepository, ParamLocalTrabMapper paramLocalTrabMapper) {

    this.paramLocalTrabalhoRepository = paramLocalTrabalhoRepository;
    this.paramLocalTrabMapper = paramLocalTrabMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetLocalTrabalhoAtivosQuery query) {
     var paramLocalTrabalhos =  paramLocalTrabalhoRepository.findAllActive();
     List<ParametrizacaoDTO> parametrizacoes = paramLocalTrabalhos.stream()
         .map(paramLocalTrabMapper::toParametrizacaoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
