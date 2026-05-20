package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.ColaboradoresAumentoRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AumentoSimulacaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AumentoSimulacaoEntityRepository extends
    JpaRepository<AumentoSimulacaoEntity, Long>,
    JpaSpecificationExecutor<AumentoSimulacaoEntity> {

  default AumentoSimulacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("AumentoSimulacaoEntity not found for id: " + id));
  }

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.ColaboradoresAumentoRowDTO(
         a.fun.nome,
         a.carreira.carrPccsId.nome,
         a.nivelEscalao,
         a.salarioAntes,
         a.salarioDepois
      )
      FROM AumentoSimulacaoEntity a
      WHERE (:institutoId IS NULL OR a.institId = :institutoId)
      AND (:seccaoId IS NULL OR a.secaoId = :seccaoId)
      """)
  Page<ColaboradoresAumentoRowDTO> list(
      @Param("institutoId") Long institutoId,
      @Param("seccaoId") Long seccaoId,
      Pageable pageable
  );

}
