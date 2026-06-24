package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.progressaopromocao.application.dto.ProgressaoPromocaoRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SimEvolucaoCarreiraEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface SimEvolucaoCarreiraEntityRepository extends
    JpaRepository<SimEvolucaoCarreiraEntity, Long>,
    JpaSpecificationExecutor<SimEvolucaoCarreiraEntity> {

  default SimEvolucaoCarreiraEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SimEvolucaoCarreiraEntity not found for id: " + id));
  }

  @Query("""
      SELECT new cv.inps.rh.progressaopromocao.application.dto.ProgressaoPromocaoRowDTO(
          e.uuid,
          e.tipo,
          e.dataReferente,
          f.nome,
          cd.cargoId.nome,
          c.nome,
          ed.paramCarrId.nome,
          ep.paramCarrId.nome,
          e.observacao,
          e.avaliacaoMedia,
          e.flgHistorico
      )
      FROM SimEvolucaoCarreiraEntity e
      JOIN e.tiprel tr
      JOIN tr.funId f
      LEFT JOIN e.carreiraIdDe cd
      LEFT JOIN tr.cargoId c
      LEFT JOIN e.escalaoIdDe ed
      LEFT JOIN e.escalaoIdPara ep
      WHERE (:tipo IS NULL OR e.tipo = :tipo)
        AND (:dataDe IS NULL OR e.dataReferente >= :dataDe)
        AND (:dataAte IS NULL OR e.dataReferente <= :dataAte)
        AND (:carreiraId IS NULL OR cd.id = :carreiraId)
        AND (:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
        AND (:funcionarioId IS NULL OR f.uuid = : funcionarioId)
      """)
  Page<ProgressaoPromocaoRowDTO> findProgressaoPromocaoWithFilters(
      @Param("tipo") String tipo,
      @Param("dataDe") LocalDate dataDe,
      @Param("dataAte") LocalDate dataAte,
      @Param("nome") String nome,
      @Param("funcionarioId") UUID funcionarioId,
      @Param("carreiraId") UUID carreiraId,
      Pageable pageable
  );

}



