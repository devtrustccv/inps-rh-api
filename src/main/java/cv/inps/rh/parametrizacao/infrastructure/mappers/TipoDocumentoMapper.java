package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.TipoDocumentoDTO;
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
    entity.setId(tipoDocumento.id());
    entity.setUuid(tipoDocumento.uuid().valor());
    entity.setReferencia(tipoDocumento.referencia());
    entity.setCodigo(tipoDocumento.codigo());
    entity.setNome(tipoDocumento.nome());
    entity.setEstado(tipoDocumento.estado());
    return entity;
  }

  public TipoDocumentoDTO toParametrizacaoDto(TipoDocumento domain) {
    if (domain == null) return null;

    TipoDocumentoDTO dto = new TipoDocumentoDTO();
    dto.setLabel(domain.nome());
    dto.setValue(domain.id());
    dto.setCodigo(domain.codigo());
    return dto;
  }


}
