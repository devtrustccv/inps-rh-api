package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.Banco;

import java.util.List;

public interface BancoRepository {

  public List<Banco> findAllActive();
}
