package cv.inps.rh.funcionario.infrastructure.mappers;


import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentoMapper {

  private final EntityManager entityManager;

  public DocumentoEntity toEntity(cv.inps.rh.shared.application.dto.AnexoReqDTO dto,
                                  Estado estado,
                                  String referenciaName,
                                  Long referenciaId,
                                  UUID referenciaUuid,
                                  Long docId,
                                  FuncionarioEntity fun) {
    if (dto == null)
      return null;
    DocumentoEntity entity = new DocumentoEntity();
    entity.setTpDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
    entity.setEstado(estado);
    entity.setUrl(dto.getDocumento());
    entity.setReferenciaName(referenciaName);
    entity.setReferenciaId(referenciaId != null ? referenciaId.toString() : null);
    entity.setReferenciaUuid(referenciaUuid);
    entity.setDocId(docId);
    entity.setFunId(fun);
    return entity;
  }

  public java.util.List<DocumentoEntity> syncDocumentos(java.util.List<DocumentoEntity> existingList,
      java.util.List<AnexoReqDTO> newList,
      String referenciaName,
      Long referenciaId,
      UUID referenciaUuid,
      Long docId,
      FuncionarioEntity fun) {
    if (newList == null)
      return existingList;
    for (AnexoReqDTO dto : newList) {
      DocumentoEntity found = null;
      if (dto.getId() != null) {
        for (DocumentoEntity d : existingList) {
          if (java.util.Objects.equals(d.getId(), dto.getId())) {
            found = d;
            break;
          }
        }
      }
      if (found != null) {
        if (dto.getTipoDocumentoId() != null) {
          found.setTpDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
        }
        found.setUrl(dto.getDocumento());
        found.setReferenciaName(referenciaName);
        found.setReferenciaId(referenciaId != null ? referenciaId.toString() : null);
        found.setReferenciaUuid(referenciaUuid);
        found.setDocId(docId);
        if (fun != null && found.getFunId() == null) {
          found.setFunId(fun);
        }
      } else {
        DocumentoEntity novo = toEntity(dto, Estado.P, referenciaName, referenciaId, referenciaUuid, docId, fun);
        existingList.add(novo);
      }
    }

    // Soft delete for removed items
    for (DocumentoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }

  public AnexoRespDTO toRespDto(DocumentoEntity d) {
    AnexoRespDTO ar = new AnexoRespDTO();
    ar.setId(d.getId());
    ar.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
    ar.setTipoDocumentoDesc(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getNome() : null);
    ar.setDocumento(d.getUrl());
    return ar;
  }

  public List<AnexoRespDTO> toAnexoRespDTOList(java.util.List<DocumentoEntity> documentos) {
    return documentos.stream().map(this::toRespDto).toList();
  }

}
