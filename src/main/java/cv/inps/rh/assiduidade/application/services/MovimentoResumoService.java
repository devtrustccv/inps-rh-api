package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.AssiduidadeListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaMovimentosResumidosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovimentoResumoService {

  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaAssiduidadadeDTO getListaMovimentosResumidos(GetListaMovimentosResumidosQuery query) {

    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    Specification<AssiduidadeSinteseDiarioEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "data"));
    Page<AssiduidadeSinteseDiarioEntity> page = sinteseRepository.findAll(spec, pageable);


    return null;

  }


  private Specification<AssiduidadeSinteseDiarioEntity> buildSpec(GetListaMovimentosResumidosQuery query) {
    return (root, cq, cb) -> {
      var predicates = new java.util.ArrayList<Predicate>();

      if (StringUtils.hasText(query.getColaborador())) {
        predicates.add(
            cb.like(cb.lower(root.get("funcionarioId").get("nome")), "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("data"), di));
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("data"), df));
      }

      if (StringUtils.hasText(query.getEstado())) {
        try {
          var estado = Estado.valueOf(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (IllegalArgumentException ignored) {
        }
      }


      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }


}
