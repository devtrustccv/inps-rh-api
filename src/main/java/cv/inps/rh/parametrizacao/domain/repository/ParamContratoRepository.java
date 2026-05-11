package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamContrato;

import java.util.List;

public interface ParamContratoRepository {

  List<ParamContrato> findAllActive();
  List<ParamContrato> findAllActive(Long paramVinculoId);
}
