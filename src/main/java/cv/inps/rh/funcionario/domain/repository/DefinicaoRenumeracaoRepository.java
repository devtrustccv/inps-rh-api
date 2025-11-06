package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.RenumeracaoFilter;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;

import java.util.List;

public interface DefinicaoRenumeracaoRepository {

  public List<DefinicaoRemuneracao> findAll(RenumeracaoFilter filter);
}
