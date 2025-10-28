package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import org.springframework.stereotype.Component;

@Component
public class ParamCarreiraMapper {


  public ParamCarreira toDomain(ParamCarreiraEntity entity) {
    if (entity == null) return null;
    return ParamCarreira.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        entity.getCodigo(),
        entity.getEstado()
    );
  }
  public ParamCarreira toDomain(Long idParamCarreira) {
    if (idParamCarreira == null || idParamCarreira < 0) return null;

    return ParamCarreira.rebuild(idParamCarreira);
  }
  public ParamCarreiraEntity toEntity(ParamCarreira domain) {
    if (domain == null) return null;

    ParamCarreiraEntity entity = new ParamCarreiraEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setNome(domain.getNome());
    entity.setCodigo(domain.getCodigo());
    entity.setEstado(domain.getEstado());
    return entity;
  }
}
