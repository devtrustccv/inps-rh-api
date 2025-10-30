package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamContrato;
import cv.inps.rh.parametrizacao.domain.repository.ParamContratoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamContratoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamContratoRepositoryImpl implements ParamContratoRepository {

  private final ParamContratoEntityRepository paramContratoEntityRepository;
  private final ParamContratoMapper paramContratoMapper;

  @Override
  public List<ParamContrato> findAllActive() {
    return paramContratoEntityRepository.findAllByEstado(Estado.A).stream().map(paramContratoMapper::toDomain).toList();
  }
}
