package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.CarreiraFilter;
import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;

import java.util.List;

public interface CarreiraRepository {

  public List<CarreiraList> findAll(CarreiraFilter filters);
}
