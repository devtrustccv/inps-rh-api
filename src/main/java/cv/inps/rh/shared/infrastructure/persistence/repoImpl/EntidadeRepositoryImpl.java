package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.Entidade;
import cv.inps.rh.shared.domain.repository.EntidadeRepository;
import cv.inps.rh.shared.infrastructure.mappers.EntidadeMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.EntidadeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EntidadeRepositoryImpl implements EntidadeRepository {

  private final EntidadeEntityRepository entidadeEntityRepository;
  private final EntidadeMapper entidadeMapper;

  @Override
  public List<Entidade> findAllActive() {
    return entidadeEntityRepository.findAll().stream().map( entidadeMapper::toDomain ).toList();
  }
}
