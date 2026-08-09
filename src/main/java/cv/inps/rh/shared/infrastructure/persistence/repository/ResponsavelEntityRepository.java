package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.configuracao.application.services.model.ResponsavelSectionData;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ResponsavelEntityRepository extends
    JpaRepository<ResponsavelEntity, Long>,
    JpaSpecificationExecutor<ResponsavelEntity> {

  default ResponsavelEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ResponsavelEntity not found for id: " + id));
  }

  List<ResponsavelEntity> findAllByInstitId_id(Long institutoId);

  List<ResponsavelEntity> findAllByInstitId_idAndSecaoId_uuid(Long institutoId, UUID seccaoUuid);

  Optional<ResponsavelEntity> findByFunId_Uuid(UUID funcionarioUuid);

  @Query("""
          SELECT NEW cv.inps.rh.configuracao.application.services.model.ResponsavelSectionData(
                s.uuid,
                s.nome,
                r.id,
                r.funId.uuid,
                r.funId.nome
          )
          FROM SecaoEntity s
          LEFT JOIN ResponsavelEntity r
              ON r.secaoId.id = s.id
              AND r.institId.id = :instId
              AND r.estado = 'A'
          LEFT JOIN FuncionarioEntity f
                ON r.funId.id = f.id
          WHERE s.instId.id = :instId
      """)
  List<ResponsavelSectionData> findAllSectionsByDirection(Long instId);
}
