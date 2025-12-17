package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.parametrizacao.domain.repository.ParamCarreiraRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCarreiraMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamCarreiraRepositoryImpl implements ParamCarreiraRepository {

  private final ParamCarreiraEntityRepository paramCarreiraEntityRepository;
  private final ParamCarreiraMapper paramCarreiraMapper;

  @Override
  public List<ParamCarreira> findAllActive() {
    return paramCarreiraEntityRepository.findAllByEstado(Estado.A).stream().map(paramCarreiraMapper::toDomain).toList();
  }
}
