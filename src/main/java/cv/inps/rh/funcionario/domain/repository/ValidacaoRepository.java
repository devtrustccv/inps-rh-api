package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.ValidacoesFilters;
import cv.inps.rh.funcionario.domain.models.Validacao;

import java.util.List;

public interface ValidacaoRepository {
  public List<Validacao> findAll(ValidacoesFilters filters);
}
