package cv.inps.rh.shared.infrastructure.persistence.repository;

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
public interface TiposRelacionamentoEntityRepository extends JpaRepository<TiposRelacionamentoEntity, Long>, JpaSpecificationExecutor<TiposRelacionamentoEntity> {

  /**
   * Tiprel CORRENTE deste contrato (est_act_adm=1). Um contrato pode ter vários tiprels ao longo do
   * tempo (mudança de situação laboral, progressão, mudança de carreira), mas só um é o corrente.
   *
   * <p>NÃO usar max(id) para o encontrar: o fluxo de progressão cria o candidato como um tiprel NOVO
   * (id maior que o corrente) e esse candidato fica na tabela para sempre — quer seja validado, quer
   * fique pendente (P), quer seja rejeitado (I/"Não Validado"). Só quando é validado é que passa ele
   * próprio a est_act_adm=1; nos outros casos max(id) aponta para um tiprel que NUNCA foi o corrente.
   *
   * <p>Serve os dois sentidos do ativar/desativar contrato porque a desativação preserva o
   * est_act_adm=1 do tiprel (só baixa o estado para I) — ver AlterarEstadoContratoService.
   */
  Optional<TiposRelacionamentoEntity> findFirstByContrVinculoId_UuidAndEstActAdm(
      UUID contratoUuid, Integer estActAdm);

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

  /**
   * Tiprel associado a uma carreira INDEPENDENTEMENTE do est_act_adm (inclui pendentes/parqueados).
   * Cada carreira tem o seu tiprel; em empate devolve o mais recente. Usado no fluxo em que o
   * pendente já tem tiprel (est_act_adm=0) — o findByCarreiraId_uuid só devolve o ativo (est_act_adm=1).
   */
  Optional<TiposRelacionamentoEntity> findFirstByCarreiraId_UuidOrderByIdDesc(UUID carreiraUuid);

  /**
   * Movimento (tiprel) derivado de {@code tiprelId} num dado estado — usado para detetar uma
   * alteração de escalão/cargo (Gestão Laboral) ainda pendente sobre o tiprel atual.
   */
  Optional<TiposRelacionamentoEntity> findFirstByTiprelId_IdAndEstado(Long tiprelId, Estado estado);

  @Query("""
      select t
      from TiposRelacionamentoEntity t
      left join fetch t.contrVinculoId c
      left join fetch c.tpContratoId
      left join fetch c.vinculoId
      left join fetch t.situacLaboralId sl
      left join fetch sl.situacaoLaboralId
      left join fetch t.cargoId
      left join fetch t.mobId m
      left join fetch m.instidId
      left join fetch m.secaoId
      left join fetch m.localTrabId
      left join fetch t.carreiraId car
      left join fetch car.carrPccsId
      left join fetch car.categoriaId
      left join fetch car.escalaoId
      left join fetch t.regimeId
      where t.funId.uuid = :funcionarioUuid
        and t.estActAdm = 1
      order by t.id, t.dataInicio desc
      """)
  Optional<TiposRelacionamentoEntity> findAtualByFuncionarioUuid(@Param("funcionarioUuid") UUID funcionarioUuid);

  @Query("""
      SELECT t
      FROM TiposRelacionamentoEntity t
      WHERE t.funId.uuid IN (:funcionarioUuids)
        AND t.estActAdm = 1
        AND t.funId.estado = 'A'
      ORDER BY t.id, t.dataInicio DESC
      """)
  List<TiposRelacionamentoEntity> findRelacionamentosAtuaisByFuncionarioUuids(@Param("funcionarioUuids") List<UUID> uuids);

  @Query("""
      select t
      from TiposRelacionamentoEntity t
      left join fetch t.contrVinculoId c
      left join fetch c.tpContratoId
      left join fetch c.vinculoId
      where t.funId.uuid = :funcionarioUuid
        and t.estActAdm = 1
      order by t.id, t.dataInicio desc
      """)
  List<TiposRelacionamentoEntity> findAllAtivosComboByFuncionarioUuid(@Param("funcionarioUuid") UUID funcionarioUuid);

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
       AND t.situacLaboralId.situacaoLaboralId.codigo = 'LIC_SV'
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
             f.uuid,
             f.nome,
             f.numDocumento,
             i.nome,
             s.nome,
             i.id,
             i.nome,
             cat.nome,
             pcc.nome,
             c.salario,
             cargo.nome,
             esc.valor,
             esc.nivelReferencia,
             esc.escalao
      )
      FROM TiposRelacionamentoEntity t
       JOIN t.funId f
      LEFT JOIN t.mobId m
      LEFT JOIN m.instidId i
      LEFT JOIN m.secaoId s
      LEFT JOIN t.carreiraId c
      LEFT JOIN c.carrPccsId pcc
      LEFT JOIN c.categoriaId cat
      LEFT JOIN c.cargoId cargo
      LEFT JOIN c.escalaoId esc
      WHERE t.estActAdm = 1
         AND f.estado NOT IN (Estado.I, Estado.P)
         AND (:processado IS NULL OR t.flgProcessa = :processado)
         AND (:directionId IS NULL OR i.id = :directionId)
         AND (:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
         AND (:uuidFuncionario IS NULL OR f.uuid = :uuidFuncionario)
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
      FROM DirecaoEntity t
      WHERE :nome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
      """)
  Page<PesquisaCentroCustoResponseDTO> pesquisaCentroCusto(
      @Param("nome") String nome,
      Pageable pageable);

  @Query("""
      select tr
      from TiposRelacionamentoEntity tr
      left join fetch tr.contrVinculoId c
      left join fetch c.tpContratoId
      left join fetch c.vinculoId
      left join fetch tr.situacLaboralId sl
      left join fetch sl.situacaoLaboralId
      left join fetch tr.cargoId
      left join fetch tr.mobId m
      left join fetch m.instidId
      left join fetch m.secaoId
      left join fetch m.localTrabId
      left join fetch tr.carreiraId car
      left join fetch car.carrPccsId
      left join fetch car.categoriaId
      left join fetch car.escalaoId
      left join fetch tr.regimeId
      where tr.funId.uuid = :funUuid
        and c.uuid = :contratoUuid
        and tr.estActAdm = 1
      """)
  TiposRelacionamentoEntity findByFunUuidAndContratoUuid(
      @Param("funUuid") UUID funUuid,
      @Param("contratoUuid") UUID contratoUuid);

  /**
   * Tiprel do contrato por TIPO_SITUACAO (ex.: 'INICIO' para a vista "Ver Informação Inicial").
   * ORDER BY id para escolher o 1º de forma determinística caso exista mais que um.
   */
  @Query("""
      select tr
      from TiposRelacionamentoEntity tr
      left join fetch tr.contrVinculoId c
      left join fetch c.tpContratoId
      left join fetch c.vinculoId
      left join fetch tr.situacLaboralId sl
      left join fetch sl.situacaoLaboralId
      left join fetch tr.cargoId
      left join fetch tr.mobId m
      left join fetch m.instidId
      left join fetch m.secaoId
      left join fetch m.localTrabId
      left join fetch tr.carreiraId car
      left join fetch car.carrPccsId
      left join fetch car.categoriaId
      left join fetch car.escalaoId
      left join fetch tr.regimeId
      where tr.funId.uuid = :funUuid
        and c.uuid = :contratoUuid
        and tr.tipoSituacao = :tipoSituacao
      order by tr.id
      """)
  java.util.List<TiposRelacionamentoEntity> findByFunUuidAndContratoUuidAndTipoSituacao(
      @Param("funUuid") UUID funUuid,
      @Param("contratoUuid") UUID contratoUuid,
      @Param("tipoSituacao") String tipoSituacao);

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
        EST_ACT_ADM AS situacaoAtual,
        ESCALAO_DESC AS escalaoDesc,
        ESCALAO_ID AS escalaoId,
        FLG_SALARIO AS flgSalario,
        DATA_CARREIRA AS dataCarreira,
        DATA_CONTRATO AS dataContrato,
        CARGO_DESC AS cargoDesc,
        CARGO_ID AS cargoId,
        SITUACAO_LABORAL_DESC AS situacaoLaboralDesc,
        SITUACAO_LABORAL_ID AS situacaoLaboralId,
        TIPREL_ID AS tiprelId,
        TIPREL_UUID AS tiprelUuid,
        PROCESSAMENTO AS processamento
      FROM RH_V_RELACAO_LABORAL
      WHERE FUNCIONARIO_UUID = :funcionarioUuid
        AND (:situacaoAtual IS NULL OR EST_ACT_ADM = :situacaoAtual)
      """, nativeQuery = true)
  List<RelacaoLaboralView> relacaoLaboralFromViewByFuncionario(@Param("funcionarioUuid") String funcionarioUuid,
                                                               @Param("situacaoAtual") Integer situacaoAtual);

}
