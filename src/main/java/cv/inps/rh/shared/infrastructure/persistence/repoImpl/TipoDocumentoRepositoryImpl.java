package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.TipoDocumento;
import cv.inps.rh.shared.domain.repository.TipoDocumentoRepository;
import cv.inps.rh.shared.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoDocumentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TipoDocumentoRepositoryImpl implements TipoDocumentoRepository {

  private final TipoDocumentoEntityRepository tipoDocumentoEntityRepository;
  private final TipoDocumentoMapper tipoDocumentoMapper;

  @Override
  public Optional<TipoDocumento> findById(Long id) {
    return tipoDocumentoEntityRepository.findById(id)
        .map(tipoDocumentoMapper::toDomain);
  }
}
