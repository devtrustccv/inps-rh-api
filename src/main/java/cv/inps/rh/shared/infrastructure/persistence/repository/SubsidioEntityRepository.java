package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.SubsidioNatalResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubsidioEntityRepository extends
    JpaRepository<SubsidioEntity, Long>,
    JpaSpecificationExecutor<SubsidioEntity> {

  default SubsidioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SubsidioEntity not found for id: " + id));
  }

  @Modifying
  @Query("""
          UPDATE SubsidioEntity s
          SET s.estado = :estado
          WHERE s.id = :subsidioId
      """)
  void updateEstadoById(@Param("subsidioId") Long subsidioId, @Param("estado") String estado);


  /**
   * Lista Subsídio de Natal: os registos em vigor (ESTADO P = por validar, A = validado; os E são
   * versões substituídas por uma alteração). Direção = a do vínculo activo do colaborador.
   */
  @Query(value = """
      SELECT NEW cv.inps.rh.processamento.application.dto.SubsidioNatalResponseDTO(
          s.id, f.id, f.nome, s.valorSalarioBase, s.mesTrab, s.percSalario, s.faltas, s.percFalta,
          s.valorSubsidio, s.valorChequeBrinde, s.valorPrendaNatal, s.flgAtivoInactivo, s.estado, s.observacao,
          CASE s.flgAtivoInactivo WHEN 'A' THEN 'Ativo' WHEN 'I' THEN 'Inativo' WHEN 'PR' THEN 'Processado' END,
          CASE s.estado WHEN 'A' THEN 'Validado' WHEN 'P' THEN 'Pendente' END
      )
      FROM SubsidioEntity s JOIN s.fun f
      WHERE s.tipoSubsidio = 'NATAL'
        AND s.anoReferente = :ano
        AND s.estado IN ('P', 'A')
        AND (:funId IS NULL OR f.id = :funId)
        AND (:direcaoId IS NULL OR EXISTS (
            SELECT 1 FROM TiposRelacionamentoEntity tr
            WHERE tr.funId = f AND tr.estActAdm = 1 AND tr.mobId.instidId.id = :direcaoId))
      ORDER BY f.nome
      """,
      countQuery = """
      SELECT COUNT(s) FROM SubsidioEntity s JOIN s.fun f
      WHERE s.tipoSubsidio = 'NATAL'
        AND s.anoReferente = :ano
        AND s.estado IN ('P', 'A')
        AND (:funId IS NULL OR f.id = :funId)
        AND (:direcaoId IS NULL OR EXISTS (
            SELECT 1 FROM TiposRelacionamentoEntity tr
            WHERE tr.funId = f AND tr.estActAdm = 1 AND tr.mobId.instidId.id = :direcaoId))
      """)
  Page<SubsidioNatalResponseDTO> findSubsidioNatal(
      @Param("ano") Long ano,
      @Param("direcaoId") Long direcaoId,
      @Param("funId") Long funId,
      Pageable pageable);

  @Query("SELECT s FROM SubsidioEntity s WHERE s.id IN :ids AND s.tipoSubsidio = 'NATAL'")
  List<SubsidioEntity> findNatalByIdIn(@Param("ids") Collection<Long> ids);

  @Query("SELECT s FROM SubsidioEntity s WHERE s.id IN :ids AND s.tipoSubsidio = 'FERIA'")
  List<SubsidioEntity> findFeriasByIdIn(@Param("ids") Collection<Long> ids);

}
