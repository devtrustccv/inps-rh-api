package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import org.springframework.stereotype.Component;

@Component
public class TipoDocumentoMapper {

  public TipoDocumento toDomain(TipoDocumentoEntity entity) {
    if (entity == null) return null;

    return TipoDocumento.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getReferencia(),
        entity.getCodigo(),
        entity.getNome(),
        entity.getEstado()
    );
  }

  public TipoDocumento toDomain(Long idTipoDocumento) {
    if (idTipoDocumento == null || idTipoDocumento < 0) return null;

    return TipoDocumento.rebuild(
        idTipoDocumento);
  }


  public TipoDocumentoEntity toEntity(TipoDocumento tipoDocumento) {
    if (tipoDocumento == null) return null;

    TipoDocumentoEntity entity = new TipoDocumentoEntity();
    entity.setId(tipoDocumento.getId());
    entity.setUuid(tipoDocumento.getUuid().getValor());
    entity.setReferencia(tipoDocumento.getReferencia());
    entity.setCodigo(tipoDocumento.getCodigo());
    entity.setNome(tipoDocumento.getNome());
    entity.setEstado(tipoDocumento.getEstado());
    return entity;
  }


}
