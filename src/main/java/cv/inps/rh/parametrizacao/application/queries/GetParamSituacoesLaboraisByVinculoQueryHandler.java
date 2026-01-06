package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GetParamSituacoesLaboraisByVinculoQueryHandler implements QueryHandler<GetParamSituacoesLaboraisByVinculoQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetParamSituacoesLaboraisByVinculoQueryHandler.class);

  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;

  public GetParamSituacoesLaboraisByVinculoQueryHandler(ParamSitLaboralEntityRepository paramSitLaboralEntityRepository) {

    this.paramSitLaboralEntityRepository = paramSitLaboralEntityRepository;
  }

  @Transactional(readOnly = true)
   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamSituacoesLaboraisByVinculoQuery query) {

    LOGGER.debug("GetParamSituacoesLaboraisByVinculoQuery: {}", query);

     var result = paramSitLaboralEntityRepository.findAllByVinculoId(query.getVinculoId())
         .stream()
         .map(r -> new ParametrizacaoDTO(
             r.getParamSit().getNome(),
             r.getId()
         ))
         .toList();

    return ResponseEntity.ok(result);
  }

}
