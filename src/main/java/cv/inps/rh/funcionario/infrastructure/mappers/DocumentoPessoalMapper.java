package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.DocumentoPessoal;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoPessoalMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final EntityManager entityManager;

  public DocumentoPessoal toDomain(DocumentoPessoalEntity entity) {
    if (entity == null)
      return null;

    return DocumentoPessoal.rebuild(
        entity.getId(),
        entity.getNumDocumento(),
        entity.getTipoDocumentoId() != null
            ? tipoDocumentoMapper.toDomain(entity.getTipoDocumentoId())
            : null,
        entity.getEstado(),
        entity.getUuid()
    );
  }

  public DocumentoPessoalEntity toEntity(DocumentoPessoal domain) {
    if (domain == null)
      return null;

    var entity = new DocumentoPessoalEntity();

    entity.setId(domain.getId());
    entity.setNumDocumento(domain.getNumDocumento());
    entity.setEstado(domain.getEstado());
    entity.setUuid(domain.getUuid().getValor());

    if (domain.getTipoDocumento() != null) {
      entity.setTipoDocumentoId(
          entityManager.getReference(
              cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity.class,
              domain.getTipoDocumento().getId()
          )
      );
    }

    return entity;
  }


}
