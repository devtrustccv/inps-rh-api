package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamEscalao;
import cv.inps.rh.parametrizacao.domain.repository.ParamEscalaoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamEscalaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamEscalaoRepositoryImpl implements ParamEscalaoRepository {

  private final ParamEscalaoEntityRepository paramEscalaoEntityRepository;
  private final ParamEscalaoMapper paramEscalaoMapper;

  @Override
  public List<ParamEscalao> findAllActive() {
    return paramEscalaoEntityRepository.findAllByEstado(Estado.A).stream().map(paramEscalaoMapper::toDomain).toList();
  }
}
