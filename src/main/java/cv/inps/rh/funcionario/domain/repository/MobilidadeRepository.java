package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;

import java.util.List;

public interface MobilidadeRepository {

  public List<MobilidadeList> findAll(MobilidadeFilter filters);
}
