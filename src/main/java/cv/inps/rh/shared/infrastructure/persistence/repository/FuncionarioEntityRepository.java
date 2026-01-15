package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface FuncionarioEntityRepository extends
    JpaRepository<FuncionarioEntity, Long>,
    JpaSpecificationExecutor<FuncionarioEntity> {

  default FuncionarioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "FuncionarioEntity not found for id: " + id));
  }




  List<FuncionarioEntity> findAllByUuidIn(List<UUID> uuid);

  Optional<FuncionarioEntity> findByUuid(UUID uuid);

  default FuncionarioEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "FuncionarioEntity not found for id: " + uuid));
  }

  @Query("SELECT f FROM FuncionarioEntity f LEFT JOIN FETCH f.contratos WHERE f.uuid = :uuid")
  Optional<FuncionarioEntity> findFuncionarioWithContratos(@Param("uuid") UUID uuid);

  @Query("SELECT (COUNT(c) > 0) FROM ContratoEntity c WHERE c.funId.uuid = :funId AND c.estado = :estado")
  boolean hasActiveContrato(@Param("funId") UUID publicId, @Param("estado") Estado estado);

  @Query("SELECT (COUNT(c) > 0) FROM ContratoEntity c WHERE c.funId.uuid = :funId AND c.estado = 'A'")
  boolean hasActiveContrato(@Param("funId") UUID publicId);


  @NotNull
  @EntityGraph(attributePaths = {
      "tiposrelacionamentos",
      "tiposrelacionamentos.contrVinculoId",
      "tiposrelacionamentos.cargoId",
      "tiposrelacionamentos.institId",
      "tiposrelacionamentos.seccaoId",
      "tiposrelacionamentos.carrPccId",
      "tiposrelacionamentos.categoriaId"
  })
  Page<FuncionarioEntity> findAll(Specification<FuncionarioEntity> spec, @NotNull Pageable pageable);



  boolean existsByTipoDocumentoId_idAndNumDocumento(Long tipoDocumentoIdId, String numDocumento);

  boolean existsByTipoDocumentoId_IdAndNumDocumentoAndUuidNot(
      Long tipoDocumentoId,
      String numDocumento,
      UUID funIdUuid);

  @Query("""
    select f from FuncionarioEntity f
    left join fetch f.dadosBancarios db
    where f.uuid = :funUuid
      and db.estado in :estados
    """)
  Optional<FuncionarioEntity> findByIdAndFilterDadosBancarios(
      @Param("funUuid") UUID funUuid,
      @Param("estados") List<Estado> estados
  );


}




