package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.parametrizacao.domain.repository.TipoDocumentoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
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
