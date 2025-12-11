package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.LocalTrabalhoDTO;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamLocalTrabalhoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamLocalTrabMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetLocalTrabalhoAtivosQueryHandler implements QueryHandler<GetLocalTrabalhoAtivosQuery, ResponseEntity<List<LocalTrabalhoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetLocalTrabalhoAtivosQueryHandler.class);

   private final ParamLocalTrabalhoRepository paramLocalTrabalhoRepository;
   private final ParamLocalTrabMapper paramLocalTrabMapper;

  public GetLocalTrabalhoAtivosQueryHandler(ParamLocalTrabalhoRepository paramLocalTrabalhoRepository, ParamLocalTrabMapper paramLocalTrabMapper) {

    this.paramLocalTrabalhoRepository = paramLocalTrabalhoRepository;
    this.paramLocalTrabMapper = paramLocalTrabMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<LocalTrabalhoDTO>> handle(GetLocalTrabalhoAtivosQuery query) {
     var paramLocalTrabalhos =  paramLocalTrabalhoRepository.findAllActive();
     var parametrizacoes = paramLocalTrabalhos.stream()
         .map(paramLocalTrabMapper::toLocalTrabalhoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
