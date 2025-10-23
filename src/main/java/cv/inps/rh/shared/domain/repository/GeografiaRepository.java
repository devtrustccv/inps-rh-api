package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.Geografia;

import java.util.Optional;

public interface GeografiaRepository {

  Optional<Geografia> findById(Long id);
}
