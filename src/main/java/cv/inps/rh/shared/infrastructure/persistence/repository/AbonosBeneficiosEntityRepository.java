package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.BaixaMedicaRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AbonosBeneficiosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface AbonosBeneficiosEntityRepository extends
    JpaRepository<AbonosBeneficiosEntity, Long>,
    JpaSpecificationExecutor<AbonosBeneficiosEntity> {

  default AbonosBeneficiosEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "AbonosBeneficiosEntity not found for id: " + id));
  }

  Optional<AbonosBeneficiosEntity> findByUuid(UUID uuid);

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.BaixaMedicaRowDTO(
          t.estado,
          null,
          null,
          f.nome,
          tr.contrVinculoId.vinculoId.nome,
          tr.cargoId.nome,
          ps.nome,
          psd.motivo,
          t.dataInicio,
          t.dataFim,
          t.uuid
      )
      FROM AbonosBeneficiosEntity t, TiposRelacionamentoEntity tr
      LEFT JOIN t.funId f
      LEFT JOIN t.paramSitId ps
      LEFT JOIN t.paramSitDetId psd
      WHERE tr.funId.id = f.id AND tr.estActAdm = 1
          AND (:nomefuncionario IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nomefuncionario, '%')))
          AND (:startDate IS NULL OR t.dataInicio = :startDate)
          AND (:endDate IS NULL OR t.dataFim = :endDate)
      """)
  Page<BaixaMedicaRowDTO> getListaColaboradores(
      @Param("nomefuncionario") String nomefuncionario,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable
  );

  default AbonosBeneficiosEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("AbonosBeneficiosEntity not found for uuid: " + uuid));
  }

}
