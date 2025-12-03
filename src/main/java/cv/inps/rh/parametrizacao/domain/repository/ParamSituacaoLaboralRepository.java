package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;

import java.util.List;
import java.util.Optional;

public interface ParamSituacaoLaboralRepository {

  ParamSitLaboral getSituacaoLaboralById(Long id);

  List<ParamSitLaboral> findAllActive();

  Optional<ParamSitLaboral> findByNomeActivo();


}
