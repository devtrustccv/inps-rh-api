package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoDocumentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class EmprestimoDocumentService {

  private final DocumentoEntityRepository documentoEntityRepository;
  private final TipoDocumentoEntityRepository tipoDocumentoEntityRepository;

  public void saveDocuments(List<DocumentoDTO> documentos, FuncionarioEntity funId, String referenceId, String referenceName) {

    if (Objects.isNull(documentos) || documentos.isEmpty())
      return;

    var docs = new ArrayList<DocumentoEntity>();

    documentos.forEach(doc -> {

      final DocumentoEntity newDoc;

      if (StringUtils.hasText(doc.getId())) {
        newDoc = documentoEntityRepository.findByUuidOrThrow(UUID.fromString(doc.getId()));
      } else {
        newDoc = new DocumentoEntity();
        newDoc.setEstado(Estado.A);
        newDoc.setReferenciaName(referenceName);
        newDoc.setReferenciaId(referenceId);
        newDoc.setUuid(UuidCreator.getTimeOrderedEpoch());
        newDoc.setDocId(1L);
      }

      newDoc.setTpDocumentoId(doc.getTipoDocumentoId() != null ? tipoDocumentoEntityRepository.findByIdOrThrow(doc.getTipoDocumentoId()) : null);
      newDoc.setFunId(funId);
      newDoc.setUrl(doc.getUrl());
      docs.add(newDoc);
    });

    documentoEntityRepository.saveAll(docs);
  }

  public List<DocumentoDTO> getDocuments(FuncionarioEntity funId, List<String> references, String referenceId) {

    return documentoEntityRepository.findAllByFunIdAndReferenciaNameInAndReferenciaIdAndEstado(
            funId,
            references,
            referenceId,
            Estado.A
        ).stream()
        .map(doc -> {
          var obj = new DocumentoDTO();
          obj.setId(doc.getUuid().toString());
          var docType = doc.getTpDocumentoId();
          if (docType != null) {
            obj.setTipoDocumentoId(docType.getId());
            obj.setTipoDocumentoDesc(docType.getNome());
            obj.setReferenciaTipoDocumento(docType.getReferencia());
          }
          obj.setUrl(doc.getUrl());
          return obj;
        })
        .toList();

  }
}
