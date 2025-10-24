package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AnexoReqDTO;
import cv.inps.rh.funcionario.application.dto.AnexoRespDTO;
import cv.inps.rh.funcionario.domain.models.Documento;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentoMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public Documento toDomain(DocumentoEntity entity) {
    if (entity == null) return null;

    TipoDocumento tipoDoc = entity.getTpDocumentoId() != null
        ? tipoDocumentoMapper.toDomain(entity.getTpDocumentoId())
        : null;

    return Documento.rebuild(
        entity.getId(),
        entity.getUuid() ,
        tipoDoc,
        entity.getDocId(),
        entity.getReferenciaName(),
        entity.getReferenciaId(),
        entity.getEstado()
    );
  }

  // Domain -> Entity (usando referência do EntityManager)
  public DocumentoEntity toEntity(Documento domain) {
    if (domain == null) return null;

    DocumentoEntity entity = new DocumentoEntity();
    if(domain.getId() !=null && domain.getId()>0)
     entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());

    entity.setTpDocumentoId(entityManager.getReference(
        TipoDocumentoEntity.class,
        domain.getTipoDocumento().getId()
    ));


    entity.setDocId(domain.getDocId());
    entity.setReferenciaName(domain.getReferenciaName());
    entity.setReferenciaId(domain.getReferenciaId());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  public Documento toDomain(AnexoReqDTO dto){
    if(dto == null) return null;
    var tipoDoc = tipoDocumentoMapper.toDomain(dto.getTipoDocumentoId());
    return Documento.create(
        dto.getId(),
        tipoDoc,
        null, // todo resolveer depois
        dto.getDocumento(),
        dto.getDocumento()

    );
  }

  public List<Documento> toDocumentosDomain(List<AnexoReqDTO> dtos) {
    if (dtos == null) return null;
    return dtos.stream()
        .map(this::toDomain)
        .toList();
  }


  public AnexoRespDTO toResponseDTO(Documento documento) {
    if (documento == null) return null;

    AnexoRespDTO dto = new AnexoRespDTO();
    dto.setId(documento.getId());
    dto.setTipoDocumentoId(documento.getTipoDocumento() != null ? documento.getTipoDocumento().getId() : null);
    dto.setTipoDocumentoDesc(documento.getTipoDocumento() != null ? documento.getTipoDocumento().getNome() : null);
    dto.setDocumento(documento.getReferenciaName()); // ou documento.getReferenciaId() dependendo do uso

    return dto;
  }

  public List<AnexoRespDTO> toResponseDTOList(List<Documento> documentos) {
    if (documentos == null) return null;
    return documentos.stream()
        .map(this::toResponseDTO)
        .toList();
  }


}
