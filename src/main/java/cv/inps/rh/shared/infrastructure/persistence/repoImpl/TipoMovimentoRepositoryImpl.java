package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.TipoMovimento;
import cv.inps.rh.shared.domain.repository.TipoMovimentoRepository;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoMovimentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TipoMovimentoRepositoryImpl implements TipoMovimentoRepository {

  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final TipoMovimentoMapper tipoMovimentoMapper;

  @Override
  public List<TipoMovimento> findAll() {
    int limit = 10; // exemplo
    return tipoMovimentoEntityRepository.findLimited(limit).stream()
        .map(tipoMovimentoMapper::toDomain)
        .toList();
  }

  @Override
  public List<TipoMovimento> findAllTipoMovimentoRenumeracao() {

    return tipoMovimentoEntityRepository.findAllByTipo("REM").
        stream().map(tipoMovimentoMapper::toDomain).toList();

  }

  @Override
  public List<TipoMovimento> findAllTipoMovimentoPagamentoDesconto() {
    return tipoMovimentoEntityRepository.findAllByTipo("PAG").
        stream().map(tipoMovimentoMapper::toDomain).toList();  }
}
