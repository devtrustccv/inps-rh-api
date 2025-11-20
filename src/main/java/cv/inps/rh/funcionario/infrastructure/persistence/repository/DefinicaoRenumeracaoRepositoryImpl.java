package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.RenumeracaoFilter;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.repository.DefinicaoRenumeracaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefinicaoRenumeracaoRepositoryImpl implements DefinicaoRenumeracaoRepository {

  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;

  @Transactional(readOnly = true)
  @Override
  public List<DefinicaoRemuneracao> findAll(RenumeracaoFilter filters) {
   /*
    int pageNumber = filters.getPageNumber() != null && filters.getPageNumber() > 0 ? filters.getPageNumber() : 1;
    int pageSize = filters.getPageSize() != null && filters.getPageSize() > 0 ? filters.getPageSize() : 20;
    int startRow = (pageNumber - 1) * pageSize + 1;
    int endRow = pageNumber * pageSize;


    List<DefinicaoRemuneracaoEntity> entities = definicaoRemuneracaoEntityRepository.findAllWithFilter(
        filters.getEstado() != null ? filters.getEstado().name() : null,
        filters.getDataInicio() != null ? java.sql.Date.valueOf(filters.getDataInicio()) : null,
        filters.getDataFim() != null ? java.sql.Date.valueOf(filters.getDataFim()) : null,
        startRow,
        endRow
    );

    return entities.stream().map(definicaoRemuneracaoMapper::toDomain).toList();*/
    return null;
  }
}
