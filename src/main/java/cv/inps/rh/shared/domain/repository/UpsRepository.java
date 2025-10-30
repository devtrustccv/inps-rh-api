package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.Ups;

import java.util.List;

public interface UpsRepository {

  public List<Ups> findAllActive();
}
