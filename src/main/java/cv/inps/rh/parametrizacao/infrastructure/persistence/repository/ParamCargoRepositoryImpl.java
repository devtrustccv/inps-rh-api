package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCargo;
import cv.inps.rh.parametrizacao.domain.repository.ParamCargoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCargoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCargoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ParamCargoRepositoryImpl implements ParamCargoRepository {

  private final ParamCargoMapper paramCargoMapper;
  private final ParamCargoEntityRepository paramCargoEntityRepository;

  @Override
  public List<ParamCargo> findAllActive() {
    return paramCargoEntityRepository.findAllByEstado(Estado.A).stream()
        .map(paramCargoMapper::toDomain)
        .toList();
  }

  @Override
  public List<ParamCargo> findAllActive(Long carreiraId) {
    if (carreiraId == null) {
      return findAllActive();
    }
    return paramCargoEntityRepository.findAllByEstadoAndParamCarrId_Id(Estado.A, carreiraId).stream()
        .map(paramCargoMapper::toDomain)
        .toList();
  }

}
