package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.parametrizacao.domain.repository.ParamSituacaoLaboralRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ParamSituacaoLaboralImpl implements ParamSituacaoLaboralRepository {

  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final ParamSitLaboralMapper paramSitLaboralMapper;

  @Override
  public ParamSitLaboral getSituacaoLaboralById(Long id) {
    return paramSitLaboralMapper.toDomain(paramSitLaboralEntityRepository.findById(id)
        .orElse(null));
  }
  @Override
  public Optional<ParamSitLaboral> findByNomeActivo() {
    return paramSitLaboralEntityRepository.findAllByNome("ATIVO").stream().findFirst()
        .map(paramSitLaboralMapper::toDomain);
  }


  @Override
  public List<ParamSitLaboral> findAllActive() {
    return paramSitLaboralEntityRepository.findAllByEstado(Estado.A).stream().map(paramSitLaboralMapper::toDomain).toList();
  }
}
