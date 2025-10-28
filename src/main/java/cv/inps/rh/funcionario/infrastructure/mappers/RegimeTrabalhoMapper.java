package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegimeTrabalhoMapper {

  private final FuncionarioMapper funcionarioMapper;
  private final ContratoMapper contratoMapper;
  private final TiposRelacionamentoMapper tiposRelacionamentoMapper;

  // Entity -> Domain
  public RegimeTrabalho toDomain(RegimeTrabalhoEntity entity) {
    if (entity == null) return null;

    return RegimeTrabalho.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoRegime(),
        entity.getTipoSituacao(),
        entity.getDataFim(),
        entity.getObs(),
        entity.getEstado(),
        funcionarioMapper.toDomain(entity.getFunId()),
        contratoMapper.toDomain(entity.getContratoId()),
        tiposRelacionamentoMapper.toDomain(entity.getTiprelId())
    );
  }

  // Domain -> Entity
  public RegimeTrabalhoEntity toEntity(RegimeTrabalho domain) {
    if (domain == null) return null;

    RegimeTrabalhoEntity entity = new RegimeTrabalhoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setTipoRegime(domain.getTipoRegime());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setDataFim(domain.getDataFim());
    entity.setObs(domain.getObs());
    entity.setEstado(domain.getEstado());

    entity.setFunId(funcionarioMapper.toEntity(domain.getFuncionario()));
    entity.setContratoId(contratoMapper.toEntity(domain.getContrato()));
    entity.setTiprelId(tiposRelacionamentoMapper.toEntity(domain.getTiprel()));

    return entity;
  }
}
