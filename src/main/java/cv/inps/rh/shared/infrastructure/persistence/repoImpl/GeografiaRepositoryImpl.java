package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.repository.GeografiaRepository;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GeografiaRepositoryImpl implements GeografiaRepository {

  private final GeografiaEntityRepository geografiaEntityRepository;
  private final GeografiaMapper geografiaMapper;

  @Override
  public Optional<Geografia> findById(Long id) {
    return geografiaEntityRepository.findById(id)
        .map(geografiaMapper::toDomain);
  }

  public List<Geografia> findByNivelDetalheAndGeogrId(Long nivelDetalhe, Long geogrId) {
    return geografiaEntityRepository
        .findByNivelDetalheAndGeogrId(nivelDetalhe, geogrId)
        .stream()
        .map(geografiaMapper::toDomain)
        .toList();
  }
}
