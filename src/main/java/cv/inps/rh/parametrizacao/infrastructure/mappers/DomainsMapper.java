package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.DominioDTO;
import cv.inps.rh.parametrizacao.domain.models.Dominio;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainsMapper {

  public static Dominio toDomain(DomainEntity entity) {
    if (entity == null) return null;
    return Dominio.rebuild(
        entity.getId(),
        entity.getDominio(),
        entity.getValor(),
        entity.getDescricao(),
        entity.getReferencia(),
        entity.getEstado()
    );
  }

  public static DomainEntity toEntity(Dominio dominio) {
    if (dominio == null) return null;

    DomainEntity entity = new DomainEntity();

    if (dominio.getId() != null) {
      entity.setId(dominio.getId());
    }
    entity.setDominio(ValidationUtil.trimToNull(dominio.getDominio()));
    entity.setValor(ValidationUtil.trimToNull(dominio.getValor()));
    entity.setDescricao(ValidationUtil.trimToNull(dominio.getDescricao()));
    entity.setReferencia(ValidationUtil.trimToNull(dominio.getReferencia()));
    entity.setEstado(dominio.getEstado());

    return entity;
  }

  public DominioDTO toDto(Dominio dominio) {
    if (dominio == null) return null;
    DominioDTO dto = new DominioDTO();
    dto.setId(dominio.getId());
    dto.setLabel(dominio.getDescricao());
    dto.setValue(dominio.getValor());
    dto.setReference(dominio.getReferencia());
    return dto;
  }
}
