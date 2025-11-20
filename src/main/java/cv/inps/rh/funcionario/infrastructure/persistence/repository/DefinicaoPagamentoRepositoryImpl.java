package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.PagamentoDescontoFilter;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.domain.repository.DefinicaoPagamentoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefinicaoPagamentoRepositoryImpl implements DefinicaoPagamentoRepository {

  private final DefPagamentoMapper defPagamentoMapper;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;

  @Transactional(readOnly = true)
  @Override
  public List<DefPagamento> findAll(PagamentoDescontoFilter filters) {

   /* int pageNumber = filters.getPageNumber() != null && filters.getPageNumber() > 0 ? filters.getPageNumber() : 1;
    int pageSize = filters.getPageSize() != null && filters.getPageSize() > 0 ? filters.getPageSize() : 20;
    int startRow = (pageNumber - 1) * pageSize + 1;
    int endRow = pageNumber * pageSize;


    List<DefPagamentoEntity> entities = defPagamentoEntityRepository.findAllWithFilter(
        filters.getEstado() != null ? filters.getEstado().name() : null,
        filters.getDataInicio() != null ? java.sql.Date.valueOf(filters.getDataInicio()) : null,
        filters.getDataFim() != null ? java.sql.Date.valueOf(filters.getDataFim()) : null,
        startRow,
        endRow
    );

    return entities.stream().map(defPagamentoMapper::toDomain).toList();*/
    return  null;
  }
}
