package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.SubsidioNatalResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.VSubsidioNatalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VSubsidioNatalEntityRepository extends JpaRepository<VSubsidioNatalEntity, Long>, JpaSpecificationExecutor<VSubsidioNatalEntity> {

  @Query("""
      SELECT NEW cv.inps.rh.processamento.application.dto.SubsidioNatalResponseDTO(
          sn.nome,
          sn.salario,
          sn.mesTrab,
          sn.percSalario,
          sn.faltas,
          sn.percFalta,
          sn.valorSubsidio,
          sn.valorChequeBrinde,
          sn.valorPrendaNatal,
          sn.estado,
          sn.id
      )
      FROM VSubsidioNatalEntity sn
      WHERE sn.anoReferente = :ano
            AND (:direcaoId IS NULL OR sn.institId = :direcaoId)
            AND (:funId IS NULL OR sn.funId = :funId)
      """)
  Page<SubsidioNatalResponseDTO> findSubsidioNatalByAno(
      @Param("ano") Long ano,
      @Param("direcaoId") Long direcaoId,
      @Param("funId") Long funId,
      Pageable pageable
  );

}
