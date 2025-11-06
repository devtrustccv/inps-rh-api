package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.RegimeFilter;
import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.funcionario.domain.repository.RegimeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RegimeRepositoryImpl implements RegimeRepository {

  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;

  @Transactional(readOnly = true)
  @Override
  public List<RegimeTrabalho> findAll(RegimeFilter filters) {

    int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
    int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 10;

    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    List<RegimeTrabalhoEntity> entities = regimeTrabalhoEntityRepository.findAllWithFilter(
        filters.getTipoRegime(),
        filters.getEstado() != null ? filters.getEstado().getCode() : null,
        startRow,
        endRow
    );

    return entities.stream()
        .map(regimeTrabalhoMapper::toDomain)
        .toList();
  }


}
