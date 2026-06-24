package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.models.Direcao;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirecaoMapper {

  public Direcao toDomain(DirecaoEntity entity) {
    if (entity == null) return null;
    return Direcao.rebuild(entity.getId(), entity.getNome(), entity.getSiga());
  }

  public Direcao toDomain(Long id) {
    if (id == null) return null;
    return Direcao.rebuild(id, null, null);
  }

  public ParametrizacaoDTO toParametrizacaoDto(DirecaoEntity entity) {
    if (entity == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(entity.getNome());
    dto.setValue(entity.getId());
    return dto;
  }

}
