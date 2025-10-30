package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.Instituicao;

import java.util.List;

public interface InstituicaoRepository {

  List<Instituicao> findAllActive();
}
