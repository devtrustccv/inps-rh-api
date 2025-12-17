package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamLocalTrab;
import cv.inps.rh.parametrizacao.domain.repository.ParamLocalTrabalhoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamLocalTrabMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamLocalTrabEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamLocalTrabalhoRepositoryImpl implements ParamLocalTrabalhoRepository {

  private final ParamLocalTrabEntityRepository paramLocalTrabEntityRepository;
  private final ParamLocalTrabMapper paramLocalTrabMapper;

  @Transactional(readOnly = true)
  @Override
  public List<ParamLocalTrab> findAllActive() {
    return paramLocalTrabEntityRepository.findAllByEstado(Estado.A).stream().map(paramLocalTrabMapper::toDomain).toList();
  }
}
