package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;

import java.util.List;

public interface ParamCarreiraRepository {

   List<ParamCarreira> findAllActive(String pccsId);
}
