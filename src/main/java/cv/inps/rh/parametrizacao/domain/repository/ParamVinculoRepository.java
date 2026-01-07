package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamVinculo;

import java.util.List;

public interface ParamVinculoRepository {

  List<ParamVinculo> findAllActive();

  List<ParamVinculo> findAllActive(Long paramContratoId);
}
