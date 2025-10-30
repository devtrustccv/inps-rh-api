package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCargo;
import cv.inps.rh.shared.application.constants.Estado;

import java.util.List;

public interface ParamCargoRepository {

  List<ParamCargo> findAllByAtivo();
}
