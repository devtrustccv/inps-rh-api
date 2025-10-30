package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.ValidacoeFilters;
import cv.inps.rh.funcionario.domain.models.Validacao;
import cv.inps.rh.funcionario.domain.repository.ValidacaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ValidacaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ValidacaoRepositoryImpl implements ValidacaoRepository {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ValidacaoMapper validacaoMapper;
  private final EntityManager entityManager;

  @Override
  @Transactional(readOnly = true)
  public List<Validacao> findAll(ValidacoeFilters filters) {

    int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
    int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 20;

    int startRow = pageNumber * pageSize + 1;
    int endRow = startRow + pageSize - 1;

    List<ValidacaoEntity> entities = validacaoEntityRepository.findAllWithFilters(
        filters.getNomeColaborador(),
        filters.getTipoAccao(),
        filters.getReferenciaName(),
        filters.getDataInicio(),
        filters.getDataFim(),
        startRow,
        endRow
    );

    return entities.stream()
        .map(validacaoMapper::toDomain)
        .collect(Collectors.toList());
  }



}
