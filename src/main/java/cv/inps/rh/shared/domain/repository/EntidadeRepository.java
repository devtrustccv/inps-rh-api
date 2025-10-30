package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.Entidade;

import java.util.List;

public interface EntidadeRepository {

  public List<Entidade> findAllActive();
}
