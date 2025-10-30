package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.Instituicao;
import cv.inps.rh.shared.domain.repository.InstituicaoRepository;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InstituicaoRepositoryImpl implements InstituicaoRepository {

  private final InstituicaoEntityRepository instituicaoEntityRepository;
  private final InstituicaoMapper instituicaoMapper;

  @Override
  public List<Instituicao> findAllActive() {
    return instituicaoEntityRepository.findAll().stream().map( instituicaoMapper::toDomain ).toList();
  }
}
