package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MobilidadeRepositoryImpl implements MobilidadeRepository {

  private final MobilidadeMapper mobilidadeMapper;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;

  @Transactional(readOnly = true)
  @Override
  public List<MobilidadeList> findAll(MobilidadeFilter filters) {
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

  @Transactional(readOnly = true)
  @Override
  public Optional<Mobilidade> getMobilidadeById(IdentificadorUnico id) {
    return mobilidadeEntityRepository.findByUuid(id.getValor())
        .map(mobilidadeMapper::toDomain);

  }
}
