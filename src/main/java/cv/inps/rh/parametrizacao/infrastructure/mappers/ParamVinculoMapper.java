package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamVinculo;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamVinculoMapper {

  public ParamVinculo toDomain(ParamVinculoEntity entity) {
    if (entity == null) return null;

    return ParamVinculo.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getCodigo(),
        entity.getNome(),
        entity.getFlgCarreira(),
        entity.getFlgSalario(),
        entity.getFlgContrato(),
        entity.getFlgTempoServico(),
        entity.getEstado()
    );
  }

  // Domain -> Entity
  public ParamVinculoEntity toEntity(ParamVinculo domain) {
    if (domain == null) return null;

    ParamVinculoEntity entity = new ParamVinculoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setCodigo(domain.getCodigo());
    entity.setNome(domain.getNome());
    entity.setFlgCarreira(domain.getFlgCarreira());
    entity.setFlgSalario(domain.getFlgSalario());
    entity.setFlgContrato(domain.getFlgContrato());
    entity.setFlgTempoServico(domain.getFlgTempoServico());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  // Referência mínima (para uso em outros mappers)
  public ParamVinculo toDomain(Long idVinculo) {
    if (idVinculo == null || idVinculo < 0) return null;
    return ParamVinculo.rebuild(idVinculo);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamVinculo domain) {
    if (domain == null) return null;
    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    return dto;
  }
}
