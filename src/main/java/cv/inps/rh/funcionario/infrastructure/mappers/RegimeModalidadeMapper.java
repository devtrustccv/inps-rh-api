package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.RegimeModalidade;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import org.springframework.stereotype.Component;

@Component
public class RegimeModalidadeMapper {

  public RegimeModalidade toDomain(RegimeModalidadeEntity entity) {
    if (entity == null) return null;
    return RegimeModalidade.rebuild(
        entity.getId(),
        entity.getModalidade(),
        entity.getDiasSemana(),
        entity.getNumHoras(),
        entity.getUuid(),
        entity.getEstado()
    );
  }

  public RegimeModalidadeEntity toEntity(RegimeModalidade domain) {
    if (domain == null) return null;

    RegimeModalidadeEntity entity = new RegimeModalidadeEntity();
    entity.setId(domain.getId());
    entity.setModalidade(domain.getModalidade());
    entity.setDiasSemana(domain.getDiasSemana());
    entity.setNumHoras(domain.getNumHoras());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().getValor() : null);
    entity.setEstado(domain.getEstado() != null ? domain.getEstado() : Estado.A);
    return entity;
  }

}
