package cv.inps.rh.parametrizacao.application.queries;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import jakarta.persistence.criteria.Predicate;

@Component
public class GetParamSituacoesAtivoQueryHandler implements QueryHandler<GetParamSituacoesAtivoQuery, ResponseEntity<List<ParametrizacaoDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetParamSituacoesAtivoQueryHandler.class);

  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;

  public GetParamSituacoesAtivoQueryHandler(ParamSituacaoEntityRepository paramSituacaoEntityRepository) {

    this.paramSituacaoEntityRepository = paramSituacaoEntityRepository;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamSituacoesAtivoQuery query) {

    LOGGER.debug("GetParamSituacoesAtivoQuery: {}", query);

    Specification<ParamSituacaoEntity> spec = (root, q, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (query.getFlgSituacaoLaboral() != null) {
        predicates.add(cb.equal(
            root.get("flgSituacaoLaboral"),
            query.getFlgSituacaoLaboral()
        ));
      }

      if (StringUtils.hasText(query.getFlgAusencia())) {
        try {
          predicates.add(cb.equal(root.get("flgAusencia"), Integer.valueOf(query.getFlgAusencia())));
        } catch (NumberFormatException e) {
          LOGGER.warn("Invalid integer format for flgAusencia: {}", query.getFlgAusencia());
        }
      }

      if (StringUtils.hasText(query.getTipoAusencia())) {
        predicates.add(cb.equal(root.get("tipoAusencia"), query.getTipoAusencia()));
      }

      if (StringUtils.hasText(query.getTipoFalta())) {
        predicates.add(cb.equal(root.get("tipoFalta"), query.getTipoFalta()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    List<ParametrizacaoDTO> result =
        paramSituacaoEntityRepository.findAll(spec).stream()
            .map(r -> {
              var dto = new ParametrizacaoDTO();
              dto.setLabel(r.getNome());
              dto.setValue(r.getId());
              return dto;
            })
            .toList();


    return ResponseEntity.ok(result);
  }


}
