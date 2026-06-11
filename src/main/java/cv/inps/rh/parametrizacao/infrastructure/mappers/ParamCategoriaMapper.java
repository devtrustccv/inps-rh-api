package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamCategoria;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCategoriaEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamCategoriaMapper {

  private final EntityManager entityManager;
  private final ParamCarreiraMapper carreiraMapper;

  // Entity -> Domain
  public ParamCategoria toDomain(ParamCategoriaEntity entity) {
    if (entity == null) return null;

    var carreira = carreiraMapper.toDomain(
        entity.getParamCarrId() != null ? entity.getParamCarrId().getId() : null
    );

    return ParamCategoria.rebuild(
        entity.getId(),
        carreira,
        entity.getUuid(),
        entity.getNome(),
        entity.getCodigo(),
        entity.getEstado()
    );
  }

  // Domain -> Entity
  public ParamCategoriaEntity toEntity(ParamCategoria domain) {
    if (domain == null) return null;

    ParamCategoriaEntity entity = new ParamCategoriaEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().valor());
    entity.setNome(ValidationUtil.trimToNull(domain.getNome()));
    entity.setCodigo(ValidationUtil.trimToNull(domain.getCodigo()));
    entity.setEstado(domain.getEstado());

    entity.setParamCarrId(ValidationUtil.ref(entityManager, ParamCarreiraEntity.class,
        domain.getParamCarreira() != null ? domain.getParamCarreira().getId() : null));

    return entity;
  }

  // Referência mínima (para usar em outros mapeamentos)
  public ParamCategoria toDomain(Long idCategoria) {
    if (idCategoria == null || idCategoria < 0) return null;
    return ParamCategoria.rebuild(idCategoria);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamCategoria domain) {
    if (domain == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());  // ou domain.getCodigo() se preferires
    dto.setValue(domain.getId());
    return dto;
  }
}
