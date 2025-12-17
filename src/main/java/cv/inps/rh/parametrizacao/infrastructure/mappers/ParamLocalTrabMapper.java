package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.LocalTrabalhoDTO;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamLocalTrab;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamLocalTrabMapper {

  private final GeografiaMapper geografiaMapper;
  private final EntityManager entityManager;


  public ParamLocalTrab toDomain(ParamLocalTrabEntity entity) {
    if (entity == null) return null;

    return ParamLocalTrab.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        geografiaMapper.toDomain(entity.getPaisId()),
        geografiaMapper.toDomain(entity.getIlhaId()),
        entity.getUpsId(),
        entity.getEstado()
    );
  }

  public ParamLocalTrabEntity toEntity(ParamLocalTrab domain) {
    if (domain == null) return null;

    ParamLocalTrabEntity entity = new ParamLocalTrabEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().valor() : null);
    entity.setNome(domain.getNome());

    // Lazy referência — só obtém se existir ID
    if (domain.getPais() != null && domain.getPais().id() != null) {
      entity.setPaisId(entityManager.getReference(GeografiaEntity.class, domain.getPais().id()));
    }

    if (domain.getIlha() != null && domain.getIlha().id() != null) {
      entity.setIlhaId(entityManager.getReference(GeografiaEntity.class, domain.getIlha().id()));
    }

    entity.setUpsId(domain.getUps());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  public ParamLocalTrab toDomain(Long id) {
    if (id == null || id < 0) return null;
    return ParamLocalTrab.rebuild(id);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamLocalTrab domain) {
    if (domain == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    return dto;
  }

  public LocalTrabalhoDTO toLocalTrabalhoDto(ParamLocalTrab domain) {
    if (domain == null) return null;

    var dto = new LocalTrabalhoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    dto.setPaisId(domain.getPais().id());
    dto.setPaisDesc(domain.getPais().nome());
    dto.setIlhaId(domain.getIlha().id());
    dto.setIlhaDesc(domain.getIlha().nome());
    return dto;
  }

}
