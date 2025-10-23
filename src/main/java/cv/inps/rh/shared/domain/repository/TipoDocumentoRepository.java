package cv.inps.rh.shared.domain.repository;

import cv.inps.rh.shared.domain.models.TipoDocumento;

import java.util.Optional;

public interface TipoDocumentoRepository {

  Optional<TipoDocumento> findById(Long id);
}
