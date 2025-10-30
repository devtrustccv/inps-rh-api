package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.ValidacoeFilters;
import cv.inps.rh.funcionario.domain.models.Validacao;
import cv.inps.rh.funcionario.domain.repository.ValidacaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ValidacaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ValidacaoRepositoryImpl implements ValidacaoRepository {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ValidacaoMapper validacaoMapper;

  @Transactional(readOnly = true)
  @Override
  public List<Validacao> findAll(ValidacoeFilters filters) {

    Specification<ValidacaoEntity> spec = (root, query, cb) -> {
      var predicates = cb.conjunction();

      if (filters.getNomeColaborador() != null && !filters.getNomeColaborador().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("funId").get("nomeCompleto")), "%" + filters.getNomeColaborador().toLowerCase() + "%"));
      }

      if (filters.getTipoAccao() != null && !filters.getTipoAccao().isBlank()) {
        predicates = cb.and(predicates, cb.equal(root.get("tipoAccao"), filters.getTipoAccao()));
      }

      if (filters.getReferenciaName() != null && !filters.getReferenciaName().isBlank()) {
        predicates = cb.and(predicates, cb.equal(root.get("referenciaName"), filters.getReferenciaName()));
      }

      if (filters.getDataInicio() != null) {
        predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("createdDate"), filters.getDataInicio()));
      }

      if (filters.getDataFim() != null) {
        predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("createdDate"), filters.getDataFim()));
      }

      return predicates;
    };

    var pageable = PageRequest.of(
        filters.getPageNumber() != null ? filters.getPageNumber() : 0,
        filters.getPageSize() != null ? filters.getPageSize() : 20
    );

    Page<ValidacaoEntity> page = validacaoEntityRepository.findAll(spec, pageable);

    return page.stream()
        .map(validacaoMapper::toDomain)
        .collect(Collectors.toList());
  }

}
