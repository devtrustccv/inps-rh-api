package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.EscalaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamEscalaoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamEscalaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetEscaloesAtivosQueryHandler implements QueryHandler<GetEscaloesAtivosQuery, ResponseEntity<List<EscalaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEscaloesAtivosQueryHandler.class);

  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamEscalaoRepository paramEscalaoRepository;

  public GetEscaloesAtivosQueryHandler(ParamEscalaoMapper paramEscalaoMapper, ParamEscalaoRepository paramEscalaoRepository) {

    this.paramEscalaoMapper = paramEscalaoMapper;
    this.paramEscalaoRepository = paramEscalaoRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<EscalaoDTO>> handle(GetEscaloesAtivosQuery query) {
     var paramEscaloes =  paramEscalaoRepository.findAllActive();
     List<EscalaoDTO> parametrizacoes = paramEscaloes.stream()
         .map(paramEscalaoMapper::toParametrizacaoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
