package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCategoria;

import java.util.List;

public interface ParamCategoriaRepository {

  public List<ParamCategoria> findAllActive();
}
