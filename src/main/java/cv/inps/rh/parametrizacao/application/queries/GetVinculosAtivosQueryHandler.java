package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.VinculoDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamVinculoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetVinculosAtivosQueryHandler implements QueryHandler<GetVinculosAtivosQuery, ResponseEntity<List<VinculoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetVinculosAtivosQueryHandler.class);

  private final ParamVinculoRepository paramVinculoRepository;
  private final ParamVinculoMapper paramVinculoMapper;

  public GetVinculosAtivosQueryHandler(ParamVinculoRepository paramVinculoRepository, ParamVinculoMapper paramVinculoMapper) {

    this.paramVinculoRepository = paramVinculoRepository;
    this.paramVinculoMapper = paramVinculoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<VinculoDTO>> handle(GetVinculosAtivosQuery query) {
     LOGGER.info("GetVinculosAtivosQueryHandler.handle: {}", query);

     var paramVinculos =  query.getParamContratoId() != null && query.getParamContratoId() > 0 ?
         paramVinculoRepository.findAllActive(query.getParamContratoId()) :
         paramVinculoRepository.findAllActive();

     var parametrizacoes = paramVinculos.stream()
         .map(paramVinculoMapper::toVinculoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
