package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilters;
import cv.inps.rh.funcionario.domain.filters.ValidacoesFilters;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.models.Validacao;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;

import java.util.List;

public interface MobilidadeRepository {

  public List<MobilidadeList> findAll(MobilidadeFilters filters);
}
