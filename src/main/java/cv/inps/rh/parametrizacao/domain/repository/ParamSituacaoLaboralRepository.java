package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;

import java.util.List;
import java.util.Optional;

public interface ParamSituacaoLaboralRepository {

  public ParamSitLaboral getSituacaoLaboralById(Long id);

  public List<ParamSitLaboral> findAllActive();

  Optional<ParamSitLaboral> findByNomeActivo();


}
