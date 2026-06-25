package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.Secao;
import cv.inps.rh.shared.infrastructure.mappers.DirecaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecaoMapper {

  private final EntityManager entityManager;
  private final DirecaoMapper direcaoMapper;

  public Secao toDomain(SecaoEntity entity) {
    if (entity == null) return null;

    return Secao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        direcaoMapper.toDomain(entity.getInstId()),
        entity.getEstado()
    );
  }

  public SecaoEntity toEntity(Secao domain) {
    if (domain == null) return null;

    SecaoEntity entity = new SecaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().valor());
    entity.setNome(ValidationUtil.trimToNull(domain.getNome()));
    entity.setEstado(domain.getEstado());

    entity.setInstId(ValidationUtil.ref(entityManager, DirecaoEntity.class,
        domain.getInstId() != null ? domain.getInstId().id() : null));

    return entity;
  }

  // Referência mínima
  public Secao toDomain(Long id) {
    if (id == null || id < 0) return null;
    return Secao.rebuild(id);
  }

  public ParametrizacaoDTO toParametrizacaoDto(Secao domain) {
    if (domain == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    return dto;
  }

}
