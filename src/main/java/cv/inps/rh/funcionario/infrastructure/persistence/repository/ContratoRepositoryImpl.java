package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.ContratoFilter;
import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.funcionario.domain.repository.ContratoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

  private final ContratoEntityRepository contratoEntityRepository;
  private final ContratoMapper contratoMapper;
  private final EntityManager entityManager;


  @Transactional(readOnly = true)
  @Override
  public List<Contrato> findAll(ContratoFilter filters) {
    int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
    int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 10;

    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    List<ContratoEntity> entities = contratoEntityRepository.findAllWithPagination(
        filters.getVinculo(),
        filters.getIdFuncionario().toString(),
        startRow,
        endRow
    );

    return entities.stream()
        .map(contratoMapper::toDomain)
        .toList();

  }
}
