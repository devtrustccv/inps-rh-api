package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.parametrizacao.domain.repository.ParamCarreiraRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCarreiraMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ParamCarreiraRepositoryImpl implements ParamCarreiraRepository {

  private final ParamCarreiraEntityRepository paramCarreiraEntityRepository;
  private final ParamCarreiraMapper paramCarreiraMapper;

  @Override
  public List<ParamCarreira> findAllActive(String pccsId) {
    var entities = StringUtils.hasText(pccsId)
        ? paramCarreiraEntityRepository.findAllByEstadoAndPccsId_Uuid(Estado.A, UUID.fromString(pccsId))
        : paramCarreiraEntityRepository.findAllByEstado(Estado.A);
    return entities.stream().map(paramCarreiraMapper::toDomain).toList();
  }
}
