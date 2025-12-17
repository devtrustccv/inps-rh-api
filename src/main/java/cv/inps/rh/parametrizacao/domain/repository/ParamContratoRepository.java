package cv.inps.rh.parametrizacao.domain.repository;

import java.util.List;

import cv.inps.rh.parametrizacao.domain.models.ParamContrato;

public interface ParamContratoRepository {

  List<ParamContrato> findAllActive();
  List<ParamContrato> findAllActive(Long paramVinculoId);
}
