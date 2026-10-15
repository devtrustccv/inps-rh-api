package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
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
  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;

  public GetParamSituacoesLaboraisByVinculoQueryHandler(ParamSitLaboralEntityRepository paramSitLaboralEntityRepository,
                                                        ParamSituacaoEntityRepository paramSituacaoEntityRepository) {

    this.paramSitLaboralEntityRepository = paramSitLaboralEntityRepository;
    this.paramSituacaoEntityRepository = paramSituacaoEntityRepository;
  }

  @Transactional(readOnly = true)
   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamSituacoesLaboraisByVinculoQuery query) {

    LOGGER.debug("GetParamSituacoesLaboraisByVinculoQuery: {}", query);

     // Sem vínculo (ex.: tipos de licença em Assiduidade): lista as situações ativas diretamente,
     // com os mesmos filtros, sem passar pelas associações a vínculos.
     if (query.getVinculoId() == null) {
       var semVinculo = paramSituacaoEntityRepository.findAllByEstado(Estado.A)
           .stream()
           .filter(p -> filtrar(p, query))
           .map(p -> new ParametrizacaoDTO(p.getNome(), p.getId(), null))
           .toList();
       return ResponseEntity.ok(semVinculo);
     }

     // Mesmo critério da associação em Parametrização (ParamVinculoService):
     // associações removidas ficam com ESTADO = 'E' (soft-delete) e não podem ser listadas.
     var result = paramSitLaboralEntityRepository.findAllByVinculoIdAndEstadoNot(query.getVinculoId(), Estado.E.name())
         .stream()
         .filter(obj -> obj.getParamSit().getFlgSituacaoLaboral().equals(1))
         .filter(obj -> filtrar(obj.getParamSit(), query))
         .map(r -> new ParametrizacaoDTO(
             r.getParamSit().getNome(),
             r.getParamSit().getId(),
             null
         ))
         .toList();

    return ResponseEntity.ok(result);
  }

  private static boolean filtrar(ParamSituacaoEntity p, GetParamSituacoesLaboraisByVinculoQuery query) {
    return (Objects.isNull(query.getFlgAbonoBeneficio()) || Objects.equals(p.getFlgAbonoBeneficio(), !query.getFlgAbonoBeneficio().isEmpty() ? Integer.parseInt(query.getFlgAbonoBeneficio()) : null))
        && (Objects.isNull(query.getFlgEstadoContrato()) || Objects.equals(p.getFlgEstadoContrato(), query.getFlgEstadoContrato()))
        && (Objects.isNull(query.getCodigo()) || Objects.equals(p.getCodigo(), query.getCodigo()));
  }

}
