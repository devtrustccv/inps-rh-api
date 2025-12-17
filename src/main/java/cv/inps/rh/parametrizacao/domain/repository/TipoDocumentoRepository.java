package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoRepository {

  Optional<TipoDocumento> findById(Long id);

  List<TipoDocumento> findAllActive();
}
