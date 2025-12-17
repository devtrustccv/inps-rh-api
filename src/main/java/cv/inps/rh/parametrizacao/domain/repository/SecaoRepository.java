package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.Secao;

import java.util.List;

public interface SecaoRepository {

  List<Secao> findAllActive();

  List<Secao> findAllActiveByInstitId(Long institId);
}
