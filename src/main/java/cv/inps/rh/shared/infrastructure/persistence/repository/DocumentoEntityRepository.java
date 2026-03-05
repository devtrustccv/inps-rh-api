package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentoEntityRepository extends
    JpaRepository<DocumentoEntity, Long>,
    JpaSpecificationExecutor<DocumentoEntity> {

  default DocumentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(
            () -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DocumentoEntity not found for id: " + id));
  }

  boolean existsByTpDocumentoId(TipoDocumentoEntity tpDocumentoId);

  Optional<DocumentoEntity> findByUuid(UUID uuid);

  default DocumentoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "DocumentoEntity not found for uuid: " + uuid));
  }

  List<DocumentoEntity> findAllByReferenciaNameAndReferenciaUuid(String referenciaName, UUID referenciaUuid);

  List<DocumentoEntity> findAllByFunIdAndReferenciaNameInAndReferenciaIdAndEstado(FuncionarioEntity funId, List<String> referenciaName, String referenciaId, Estado estado);

  Optional<DocumentoEntity> findByReferenciaNameAndReferenciaUuidAndReferenciaIdAndFunIdAndEstado(
      String referenciaName,
      UUID referenciaUuid,
      String referenciaId,
      FuncionarioEntity funId,
      Estado estado
  );

}
