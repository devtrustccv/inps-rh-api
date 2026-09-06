package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.SubsidioFeriasResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.VSubsidioFeriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VSubsidioFeriaEntityRepository extends JpaRepository<VSubsidioFeriaEntity, Long>, JpaSpecificationExecutor<VSubsidioFeriaEntity> {

  @Query("""
      SELECT NEW cv.inps.rh.processamento.application.dto.SubsidioFeriasResponseDTO(
          sf.subsidioId,
          sf.funNome,
          sf.funId,
          sf.valorMesTotal,
          sf.flgAtivoInactivo,
          sf.estado,
          sf.mesesTotal || '-' || sf.diasTotal,
          sf.diasSubsidio,
          sf.valorSubsidio
      )
      FROM VSubsidioFeriaEntity sf
      WHERE sf.anoReferente = :ano
            AND (:direcaoId IS NULL OR sf.institId = :direcaoId)
            AND (:funId IS NULL OR sf.funId = :funId)
      """)
  Page<SubsidioFeriasResponseDTO> findSubsidioFeriasByAno(
      @Param("ano") Integer ano,
      @Param("direcaoId") Long direcaoId,
      @Param("funId") Long funId,
      Pageable pageable
  );

  @Procedure(procedureName = "RH_PK_SUBSISIO_NATAL_F_DB.CALCULO_DIAS_FERIA")
  void calcularDiasFeria(
      @Param("P_ANO_REFERENTE") String ano,
      @Param("P_USER_REGISTO_ID") Long userId,
      @Param("P_USER_REGISTO_NAME") String username
  );

}
