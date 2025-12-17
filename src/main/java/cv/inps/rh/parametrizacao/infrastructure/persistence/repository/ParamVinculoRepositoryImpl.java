package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamVinculo;
import cv.inps.rh.parametrizacao.domain.repository.ParamVinculoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamVinculoRepositoryImpl implements ParamVinculoRepository {

  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamVinculoEntityRepository paramVinculoEntityRepository;

  @Override
  public List<ParamVinculo> findAllActive() {
    return paramVinculoEntityRepository.findAllByEstado(Estado.A).stream().map(paramVinculoMapper::toDomain).toList();
  }
}
