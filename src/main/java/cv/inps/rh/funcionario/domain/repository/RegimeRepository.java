package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.CarreiraFilter;
import cv.inps.rh.funcionario.domain.filters.RegimeFilter;
import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;

import java.util.List;

public interface RegimeRepository {

  public List<RegimeTrabalho> findAll(RegimeFilter filters);
}
