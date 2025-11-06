package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.PagamentoDescontoFilter;
import cv.inps.rh.funcionario.domain.models.DefPagamento;

import java.util.List;

public interface DefinicaoPagamentoRepository {

  public List<DefPagamento> findAll(PagamentoDescontoFilter filter);
}
