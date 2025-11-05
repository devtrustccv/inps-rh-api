package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilters;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MobilidadeRepositoryImpl implements MobilidadeRepository {

  private final MobilidadeMapper mobilidadeMapper;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;

  @Transactional(readOnly = true)
  @Override
  public List<MobilidadeList> findAll(MobilidadeFilters filters) {
    int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
    int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 50;
    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    return mobilidadeEntityRepository.findAllMobilidades(
        filters.getTipoMobilidade(),
        filters.getDataInicio() != null ? filters.getDataInicio().toLocalDate() : null,
        filters.getDataFim() != null ? filters.getDataFim().toLocalDate() : null,
        startRow,
        endRow
    );

  }
}
