package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.parametrizacao.domain.repository.ParamSituacaoLaboralRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParamSituacaoLaboralImpl implements ParamSituacaoLaboralRepository {

  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final ParamSitLaboralMapper paramSitLaboralMapper;

  @Override
  public ParamSitLaboral getSituacaoLaboralById(Long id) {
    return null;
  }

  @Override
  public List<ParamSitLaboral> findAllActive() {
    return paramSitLaboralEntityRepository.findAllByEstado(Estado.A).stream().map(paramSitLaboralMapper::toDomain).toList();
  }
}
