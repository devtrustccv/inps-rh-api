package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamCategoria;
import cv.inps.rh.parametrizacao.domain.repository.ParamCategoriaRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCategoriaMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCategoriaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ParamCategoriaRepositoryImpl implements ParamCategoriaRepository {

  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamCategoriaEntityRepository paramCategoriaEntityRepository;

  @Override
  public List<ParamCategoria> findAllActive() {
    return paramCategoriaEntityRepository.findAllByEstado(Estado.A).stream().map(paramCategoriaMapper::toDomain).toList();
  }
}
