package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;

import java.util.List;

public interface ParamSituacaoLaboralRepository {

  public ParamSitLaboral getSituacaoLaboralById(String id);

  public List<ParamSitLaboral> findAllActive();

}
