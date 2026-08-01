package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ValidacaoEntityRepository extends JpaRepository<ValidacaoEntity, Long>, JpaSpecificationExecutor<ValidacaoEntity> {

  default ValidacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ValidacaoEntity not found for id: " + id));
  }

  Optional<ValidacaoEntity> findByUuid(UUID uuid);

  default ValidacaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("ValidacaoEntity not found for id: " + uuid));
  }

  boolean existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(UUID funIdUuid, Estado estado, String tipoAccao, String referenciaName);

  Optional<ValidacaoEntity> findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(UUID funIdUuid, Estado estado, String tipoAccao, String referenciaName);

  Optional<ValidacaoEntity> findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(UUID referenciaUuid, Estado estado, String tipoAccao, String referenciaName);

  /**
   * Nº de validações de uma referência (ex.: renovações de um contrato) num dado estado/ação.
   */
  long countByReferenciaIdAndReferenciaNameAndTipoAccaoAndEstado(Long referenciaId, String referenciaName, String tipoAccao, Estado estado);

  @Modifying
  @Transactional
  @Query("""
          UPDATE ValidacaoEntity v
             SET v.estado = :novoEstado
           WHERE v.tipoAccao = :tipoAccao
             AND v.referenciaName = :referenciaName
             AND v.referenciaUuid IN :referenciaIds
             AND v.estado = :estadoAtual
      """)
  int updateValidationsForRegularization(
      @Param("novoEstado") Estado novoEstado,
      @Param("tipoAccao") String tipoAccao,
      @Param("referenciaName") String referenciaName,
      @Param("referenciaIds") List<String> referenciaIds,
      @Param("estadoAtual") Estado estadoAtual
  );
}
