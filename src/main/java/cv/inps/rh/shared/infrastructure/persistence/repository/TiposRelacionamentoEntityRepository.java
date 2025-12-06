package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.ColaboradorResponseDTO;
import cv.inps.rh.processamento.application.dto.PesquisaColaboradorResponseDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
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
public interface TiposRelacionamentoEntityRepository extends
    JpaRepository<TiposRelacionamentoEntity, Long>,
    JpaSpecificationExecutor<TiposRelacionamentoEntity> {

  default TiposRelacionamentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "TiposRelacionamentoEntity not found for id: " + id));
  }

  boolean existsByContrVinculoId_VinculoId(ParamVinculoEntity vinculoId);

  boolean existsByCarrPccId(ParamCarreiraEntity categoriaId);

  boolean existsByEscalaoId(ParamEscalaoEntity escalaoId);

  boolean existsByLocTrabId(ParamLocalTrabEntity localTrabEntity);

  boolean existsBySeccaoId(SecaoEntity section);

  Optional<TiposRelacionamentoEntity> findByUuid(UUID uuid);

  default TiposRelacionamentoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("TiposRelacionamentoEntity not found for id: " + uuid));
  }

  TiposRelacionamentoEntity findByFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity funcionario, Estado estado);

  Page<TiposRelacionamentoEntity> findByFunId_UuidAndEstado(UUID funcionarioId, Estado estado, Pageable pageable);

  TiposRelacionamentoEntity findByCarreiraId_uuid(UUID carreiraId);

  @Query("""
      select t
      from TiposRelacionamentoEntity t
      where t.funId.uuid = :funcionarioUuid
        and t.estActAdm = 1
      order by t.id, t.dataInicio desc
      """)
  Optional<TiposRelacionamentoEntity> findAtualByFuncionarioUuid(@Param("funcionarioUuid") UUID funcionarioUuid);

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.ColaboradorResponseDTO(
               null,
               t.situacLaboralId.estado,
               t.institId.nome,
               t.seccaoId.nome,
               t.contrVinculoId.tpContratoId.nome,
               t.cargoId.nome,
               t.situacLaboralId.motivoSitLab,
               t.situacLaboralId.dataInicio,
               t.situacLaboralId.dataFim,
               t.funId.uuid,
               t.funId.nome
           )
      FROM TiposRelacionamentoEntity t
      WHERE
           (:directionId IS NULL OR t.institId.id = :directionId)
       AND (:funcionario IS NULL OR LOWER(t.funId.nome) LIKE LOWER(CONCAT('%', :funcionario, '%')))
       AND (:startDate IS NULL OR t.situacLaboralId.dataInicio = :startDate)
       AND (:endDate IS NULL OR t.situacLaboralId.dataFim = :endDate)
      """)
  Page<ColaboradorResponseDTO> getListaColaboradores(
      @Param("directionId") Long directionId,
      @Param("funcionario") String funcionario,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.PesquisaColaboradorResponseDTO(
               t.uuid,
               t.funId.nome,
               t.institId.nome,
               null
           )
      FROM TiposRelacionamentoEntity t
      WHERE t.estActAdm = 1
           AND (:directionId IS NULL OR t.institId.id = :directionId)
           AND (:nome IS NULL OR LOWER(t.funId.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      """)
  Page<PesquisaColaboradorResponseDTO> pesquisaColaborador(
      @Param("directionId") Long directionId,
      @Param("nome") String nome,
      Pageable pageable
  );
}
