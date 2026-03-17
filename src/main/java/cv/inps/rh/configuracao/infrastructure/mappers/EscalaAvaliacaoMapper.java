package cv.inps.rh.configuracao.infrastructure.mappers;

import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import org.springframework.stereotype.Component;

@Component
public class EscalaAvaliacaoMapper {

  public ParamEscalaAvaliacaoEntity toEntity(EscalaAvaliacaoRowDTO dto) {
    if (dto == null) {
      return null;
    }

    var entity = new ParamEscalaAvaliacaoEntity();
    entity.setNivel(dto.getNivel());
    entity.setQualitativa(dto.getQualitativa());
    entity.setDescricao(dto.getDescricao());
    entity.setQuantitativaDe(dto.getQuantitativaDe());
    entity.setQuantitativaAte(dto.getQuantitativaAte());
    return entity;
  }

  public EscalaAvaliacaoResponseDTO toResponse(ParamEscalaAvaliacaoEntity entity) {
    if (entity == null) {
      return null;
    }

    var dto = new EscalaAvaliacaoResponseDTO();
    dto.setId(entity.getId());
    dto.setUuid(entity.getUuid());
    dto.setNivel(entity.getNivel());
    dto.setQualitativa(entity.getQualitativa());
    dto.setDescricao(entity.getDescricao());
    dto.setQuantitativaDe(entity.getQuantitativaDe());
    dto.setQuantitativaAte(entity.getQuantitativaAte());
    dto.setEstado(entity.getEstado() != null ? entity.getEstado().getCode() : null);
    return dto;
  }
}

