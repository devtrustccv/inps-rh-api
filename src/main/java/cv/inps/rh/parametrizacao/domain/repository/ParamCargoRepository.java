package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCargo;

import java.util.List;

public interface ParamCargoRepository {

  List<ParamCargo> findAllActive();
}
