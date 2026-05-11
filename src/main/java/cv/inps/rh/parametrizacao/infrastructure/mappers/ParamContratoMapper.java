package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamContrato;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamContratoMapper {

  private final EntityManager entityManager;
  private final ParamVinculoMapper vinculoMapper;

  // Entity -> Domain
  public ParamContrato toDomain(ParamContratoEntity entity) {
    if (entity == null) return null;

    /*ParamVinculo vinculo = vinculoMapper.toDomain(
        entity.getParamVinculoId() != null ? entity.getParamVinculoId().getId() : null
    );*/

    return ParamContrato.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getCodigo(),
        entity.getNome(),
        entity.getNatureza(),
        entity.getFlgRenovavel(),
        entity.getDuracaoRenovavel(),
        entity.getPrazoObrigatorio(),
        null,
        entity.getEstado()
    );
  }

  // Domain -> Entity
  public ParamContratoEntity toEntity(ParamContrato domain) {
    if (domain == null) return null;

    ParamContratoEntity entity = new ParamContratoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().valor());
    entity.setCodigo(domain.getCodigo());
    entity.setNome(domain.getNome());
    entity.setNatureza(domain.getNatureza());
    entity.setFlgRenovavel(domain.getFlgRenovavel());
    entity.setDuracaoRenovavel(domain.getDuracaoRenovavel());
    entity.setPrazoObrigatorio(domain.getPrazoObrigatorio());
    entity.setEstado(domain.getEstado());

   /* if (domain.getParamVinculo() != null && domain.getParamVinculo().getId() != null) {
      entity.setParamVinculoId(entityManager.getReference(ParamVinculoEntity.class, domain.getParamVinculo().getId()));
    }*/

    return entity;
  }

  // Referência mínima
  public ParamContrato toDomain(Long idContrato) {
    if (idContrato == null || idContrato < 0) return null;
    return ParamContrato.rebuild(idContrato);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamContrato domain) {
    if (domain == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(domain.getNome());
    dto.setValue(domain.getId());
    return dto;
  }

}
