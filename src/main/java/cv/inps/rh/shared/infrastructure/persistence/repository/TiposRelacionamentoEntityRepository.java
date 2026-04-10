package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.HistoricoLaboralViewRow;
import cv.inps.rh.funcionario.domain.projections.RelacaoLaboralView;
import cv.inps.rh.processamento.application.dto.ColaboradorResponseDTO;
import cv.inps.rh.processamento.application.dto.PesquisaCentroCustoResponseDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TiposRelacionamentoEntityRepository extends
    JpaRepository<TiposRelacionamentoEntity, Long>,
    JpaSpecificationExecutor<TiposRelacionamentoEntity> {

  default TiposRelacionamentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "TiposRelacionamentoEntity not found for id: " + id));
  }

  boolean existsByContrVinculoId_VinculoId(ParamVinculoEntity vinculoId);

  boolean existsByCarreiraId_CarrPccsId(ParamCarreiraEntity carreiraIdCarrPccsId);

  boolean existsByCarreiraId_EscalaoId(ParamEscalaoEntity escalaoId);

  boolean existsByMobId_LocalTrabId(ParamLocalTrabEntity localTrabEntity);

  boolean existsByMobId_SecaoId(SecaoEntity section);

  Optional<TiposRelacionamentoEntity> findByUuid(UUID uuid);

  default TiposRelacionamentoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(
        () -> IgrpResponseStatusException
            .notFound("TiposRelacionamentoEntity not found for id: " + uuid));
  }

  @Query("""
      select t
      from TiposRelacionamentoEntity t
      left join fetch t.mobId m
      left join fetch m.instidId inst
      where t.estActAdm = 1
        and t.funId.uuid in :funcionarioUuids
      """)
  List<TiposRelacionamentoEntity> findAtuaisByFuncionarioUuids(@Param("funcionarioUuids") List<UUID> funcionarioUuids);

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
               t.mobId.instidId.nome,
               t.mobId.secaoId.nome,
               t.contrVinculoId.tpContratoId.nome,
               t.cargoId.nome,
               t.situacLaboralId.tipoSituacao,
               t.situacLaboralId.dataInicio,
               t.situacLaboralId.dataFim,
               t.funId.uuid,
               t.funId.nome
           )
      FROM TiposRelacionamentoEntity t
      WHERE
           (:directionId IS NULL OR t.mobId.instidId.id = :directionId)
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
               t.id,
               t.funId.uuid,
               t.funId.nome,
               t.mobId.instidId.nome,
               t.mobId.instidId.id,
               null,
               t.carreiraId.categoriaId.nome,
               t.carreiraId.salario,
               t.carreiraId.cargoId.nome,
               t.carreiraId.escalaoId.valor
           )
      FROM TiposRelacionamentoEntity t
      WHERE t.estActAdm = 1
           AND (:processado IS NULL OR t.flgProcessa = :processado)
           AND (:directionId IS NULL OR t.mobId.instidId.id = :directionId)
           AND (:nome IS NULL OR LOWER(t.funId.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
           AND (:uuidFuncionario IS NULL OR t.funId.uuid = :uuidFuncionario)
      """)
  Page<PesquisaColaboradorResponseDTO> pesquisaColaborador(
      @Param("directionId") Long directionId,
      @Param("nome") String nome,
      @Param("uuidFuncionario") UUID uuidFuncionario,
      @Param("processado") Integer processado,
      Pageable pageable);

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.PesquisaCentroCustoResponseDTO(
            t.id,
            t.nome
           )
      FROM InstituicaoEntity t
      WHERE :nome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
      """)
  Page<PesquisaCentroCustoResponseDTO> pesquisaCentroCusto(
      @Param("nome") String nome,
      Pageable pageable);

  @Query("""
      select tr
      from TiposRelacionamentoEntity tr
      where tr.funId.uuid = :funUuid
        and tr.contrVinculoId.uuid = :contratoUuid
      """)
  TiposRelacionamentoEntity findByFunUuidAndContratoUuid(
      @Param("funUuid") UUID funUuid,
      @Param("contratoUuid") UUID contratoUuid);

  @Query(value = """
      SELECT
        FUNCIONARIO_UUID AS funcionarioUuid,
        CONTRATO_DESC AS contratoDesc,
        CONTRATO_ID AS contratoId,
        VINCULO_DESC AS vinculoDesc,
        VINCULO_ID AS vinculoId,
        DIRECAO_DESC AS direcaoDesc,
        DIRECAO_ID AS direcaoId,
        SECCAO_DESC AS seccaoDesc,
        SECCAO_ID AS seccaoId,
        CARREIRA_DESC AS carreiraDesc,
        CARREIRA_ID AS carreiraId,
        ESCALAO_DESC AS escalaoDesc,
        ESCALAO_ID AS escalaoId,
        DATA_CARREIRA AS dataCarreira,
        DATA_CONTRATO AS dataContrato,
        CARGO_DESC AS cargoDesc,
        CARGO_ID AS cargoId,
        SITUACAO_LABORAL_DESC AS situacaoLaboralDesc,
        SITUACAO_LABORAL_ID AS situacaoLaboralId
      FROM RH_V_RELACAO_LABORAL
      """, nativeQuery = true)
  List<RelacaoLaboralView> relacaoLaboralFromView();

  @Query(value = """
      SELECT
        FUNCIONARIO_UUID AS funcionarioUuid,
        CONTRATO_DESC AS contratoDesc,
        CONTRATO_ID AS contratoId,
        VINCULO_DESC AS vinculoDesc,
        VINCULO_ID AS vinculoId,
        DIRECAO_DESC AS direcaoDesc,
        DIRECAO_ID AS direcaoId,
        SECCAO_DESC AS seccaoDesc,
        SECCAO_ID AS seccaoId,
        CARREIRA_DESC AS carreiraDesc,
        CARREIRA_ID AS carreiraId,
        CARREIRA_UUID AS carreiraUuid,
        EST_ACT_ADM as situacaoAtual,
        ESCALAO_DESC AS escalaoDesc,
        ESCALAO_ID AS escalaoId,
        DATA_CARREIRA AS dataCarreira,
        DATA_CONTRATO AS dataContrato,
        CARGO_DESC AS cargoDesc,
        CARGO_ID AS cargoId,
        SITUACAO_LABORAL_DESC AS situacaoLaboralDesc,
        SITUACAO_LABORAL_ID AS situacaoLaboralId
      FROM RH_V_RELACAO_LABORAL
      WHERE FUNCIONARIO_UUID = :funcionarioUuid
      """, nativeQuery = true)
  List<RelacaoLaboralView> relacaoLaboralFromViewByFuncionario(@Param("funcionarioUuid") String funcionarioUuid);

  @Query(value = """
      SELECT
        FUN_UUID AS funcionarioUuid,
        TIPREL_ID AS tiprelId,
        TIPO_CONTRATO_DESC AS tipoContratoDesc,
        VINCULO_DESC AS vinculoDesc,
        DIRECAO_DESC AS direcaoDesc,
        SECCAO_DESC AS seccaoDesc,
        CARREIRA_DESC AS carreiraDesc,
        REFERENCIA_ESCALAO_DESC AS referenciaEscalaoDesc,
        CARGO_DESC AS cargoDesc,
        SITUACAO_LABORAL_DESC AS situacaoLaboralDesc,
        DATA_INICIO AS dataInicio,
        DATA_FIM AS dataFim,
        TIPO_SITUACAO_DESC AS tipoSituacaoDesc,
        ULTIMO_VINCULO AS ultimoVinculo
      FROM RH_V_HIST_LABORAL
      WHERE FUN_UUID = :funcionarioUuid
        AND (:referencia IS NULL OR REFERENCIA = :referencia)
        AND (:tipoSituacao IS NULL OR LOWER(TIPO_SITUACAO_DESC) LIKE LOWER('%' || :tipoSituacao || '%'))
        AND (:situacaoLaboral IS NULL OR LOWER(SITUACAO_LABORAL_DESC) LIKE LOWER('%' || :situacaoLaboral || '%'))
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      ORDER BY DATA_INICIO DESC
      """, countQuery = """
      SELECT COUNT(*)
      FROM RH_V_HIST_LABORAL
      WHERE FUN_UUID = :funcionarioUuid
        AND (:referencia IS NULL OR REFERENCIA = :referencia)
        AND (:tipoSituacao IS NULL OR LOWER(TIPO_SITUACAO_DESC) LIKE LOWER('%' || :tipoSituacao || '%'))
        AND (:situacaoLaboral IS NULL OR LOWER(SITUACAO_LABORAL_DESC) LIKE LOWER('%' || :situacaoLaboral || '%'))
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      """, nativeQuery = true)
  Page<HistoricoLaboralViewRow> historicoLaboralViewByFuncionario(
      @Param("funcionarioUuid") String funcionarioUuid,
      @Param("referencia") String referencia,
      @Param("tipoSituacao") String tipoSituacao,
      @Param("situacaoLaboral") String situacaoLaboral,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      Pageable pageable);

}
