package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamEscalao;

import java.util.List;

public interface ParamEscalaoRepository {

  List<ParamEscalao> findAllActive();

  List<ParamEscalao> findAllActive(Long carreiraId);
}
