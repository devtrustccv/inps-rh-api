package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.Secao;
import cv.inps.rh.parametrizacao.domain.repository.SecaoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.SecaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SecaoRepositoryImpl implements SecaoRepository {

  private final SecaoEntityRepository secaoEntityRepository;
  private final SecaoMapper secaoMapper;

  @Override
  public List<Secao> findAllActive() {
    return secaoEntityRepository.findAllByEstado(Estado.A).stream().map(secaoMapper::toDomain).toList();
  }
}
