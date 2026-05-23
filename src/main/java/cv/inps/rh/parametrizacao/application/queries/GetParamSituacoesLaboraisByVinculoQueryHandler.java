package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
         .filter(obj -> Objects.isNull(query.getFlgAbonoBeneficio()) || obj.getParamSit().getFlgAbonoBeneficio().equals(Integer.parseInt(query.getFlgAbonoBeneficio())))
         .filter(obj -> Objects.isNull(query.getFlgEstadoContrato()) || obj.getParamSit().getFlgEstadoContrato().equals(query.getFlgEstadoContrato()))
         .map(r -> new ParametrizacaoDTO(
             r.getParamSit().getNome(),
             r.getParamSit().getId()
         ))
         .toList();

    return ResponseEntity.ok(result);
  }

}
