package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamLocalTrab;

import java.util.List;

public interface ParamLocalTrabalhoRepository {

  public List<ParamLocalTrab> findAllActive();
}
