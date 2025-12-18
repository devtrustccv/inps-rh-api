package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamSitLaboralMapper {

  public ParamSitLaboral toDomain(ParamSituacaoEntity entity) {
    if (entity == null) return null;

    return ParamSitLaboral.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getCodigo(),
        entity.getNome(),
        entity.getTipoSituacao(),
        entity.getFlgRenumeracao(),
        entity.getFlgAfetaCarreira(),
        entity.getFlgContaTempServico(),
        entity.getFlgCessaProgressao(),
        entity.getFlgEstadoContrato(),
        entity.getEstado()
    );
  }

  public ParamSituacaoEntity toEntity(ParamSitLaboral domain) {
    if (domain == null) return null;

    ParamSituacaoEntity entity = new ParamSituacaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().valor() : null);
    entity.setCodigo(domain.getCodigo());
    entity.setNome(domain.getNome());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setFlgRenumeracao(domain.getFlgRenumeracao());
    entity.setFlgAfetaCarreira(domain.getFlgAfetaCarreira());
    entity.setFlgContaTempServico(domain.getFlgContaTempServico());
    entity.setFlgCessaProgressao(domain.getFlgCessaProgressao());
    entity.setFlgEstadoContrato(domain.getFlgEstadoContrato());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  public ParamSitLaboral toDomain(Long id) {
    if (id == null || id < 0) return null;
    return ParamSitLaboral.rebuild(id);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamSitLaboral domain) {
    if (domain == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    return dto;
  }

}
