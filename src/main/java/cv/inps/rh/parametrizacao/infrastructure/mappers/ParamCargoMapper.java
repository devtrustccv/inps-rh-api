package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamCargo;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamCargoMapper {

  private final EntityManager entityManager;
  private final ParamCarreiraMapper carreiraMapper;

  // Entity -> Domain
  public ParamCargo toDomain(ParamCargoEntity entity) {
    if (entity == null) return null;

    var carreira = carreiraMapper.toDomain(
        entity.getParamCarrId() != null ? entity.getParamCarrId().getId() : null
    );

    return ParamCargo.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        carreira,
        entity.getDirigente(),
        entity.getEstado()
    );
  }

  // Domain -> Entity
  public ParamCargoEntity toEntity(ParamCargo domain) {
    if (domain == null) return null;

    ParamCargoEntity entity = new ParamCargoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().valor());
    entity.setNome(domain.getNome());
    entity.setDirigente(domain.getDirigente());
    entity.setEstado(domain.getEstado());

    if (domain.getCarreira() != null && domain.getCarreira().getId() != null) {
      entity.setParamCarrId(entityManager.getReference(ParamCarreiraEntity.class, domain.getCarreira().getId()));
    }

    return entity;
  }

  // Referência mínima (para usar em outros mapeamentos)
  public ParamCargo toDomain(Long idCargo) {
    if (idCargo == null || idCargo < 0) return null;
    return ParamCargo.rebuild(idCargo);
  }

  public ParametrizacaoDTO toParametrizacaoDto(ParamCargo paramCargo){
     var parametrizacaoDTO = new ParametrizacaoDTO();
     parametrizacaoDTO.setValue(paramCargo.getId());
     parametrizacaoDTO.setLabel(paramCargo.getNome());
     return parametrizacaoDTO;
  }
}
