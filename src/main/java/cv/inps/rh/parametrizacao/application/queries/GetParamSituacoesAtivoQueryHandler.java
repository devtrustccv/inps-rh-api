package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;

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
      if (query.getFlgSituacaoLaboral() == null) {
        return cb.conjunction();
      }
      return cb.equal(
          root.get("flgSituacaoLaboral"),
          query.getFlgSituacaoLaboral()
      );
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
