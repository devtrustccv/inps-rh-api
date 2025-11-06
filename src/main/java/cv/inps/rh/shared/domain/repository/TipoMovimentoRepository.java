package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.TipoMovimento;

import java.util.List;

public interface TipoMovimentoRepository {

  public List<TipoMovimento> findAll();

  public List<TipoMovimento> findAllTipoMovimentoRenumeracao();

  public List<TipoMovimento> findAllTipoMovimentoPagamentoDesconto();

}
