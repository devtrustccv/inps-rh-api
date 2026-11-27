package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

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

  Optional<FuncionarioEntity> findByUuid(UUID uuid);

  /** Número de colaborador (RH_T_FUNCIONARIOS.ID_COLABORADOR); não é único na BD, por isso devolve lista. */
  List<FuncionarioEntity> findAllByIdColaborador(Long idColaborador);

  default FuncionarioEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "FuncionarioEntity not found for id: " + uuid));
  }

  @Query("SELECT f FROM FuncionarioEntity f LEFT JOIN FETCH f.contratos WHERE f.uuid = :uuid")
  Optional<FuncionarioEntity> findFuncionarioWithContratos(@Param("uuid") UUID uuid);

  /**
   * Lê o colaborador com {@code SELECT ... FOR UPDATE}, para serializar quem consome os
   * saldos dele (férias, dispensa, faltas).
   *
   * <p>Os saldos são sempre um ler-decidir-gravar: lê-se quanto resta, decide-se o que cabe e
   * grava-se o consumo. Duas transacções em paralelo lêem o mesmo valor antes de qualquer uma
   * gravar e concedem as duas — ficando gozados mais dias do que o direito. A linha do
   * colaborador é o ponto de serialização natural porque é o dono de todos esses saldos.
   *
   * <p>Não confundir com a reserva: a reserva evita a surpresa de dias entre o pedido e o
   * despacho, este lock evita a corrida de milissegundos entre duas escritas simultâneas.
   *
   * @see cv.inps.rh.shared.domain.service.SaldoLockService
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT f FROM FuncionarioEntity f WHERE f.uuid = :uuid")
  Optional<FuncionarioEntity> lockByUuid(@Param("uuid") UUID uuid);

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

  boolean existsByNifAndEstadoNot(Long nif, Estado estado);

  boolean existsByNifAndUuidNotAndEstadoNot(Long nif, UUID uuid, Estado estado);

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



  /**
   * B1 (QA 26/11): unicidade de documento/NIF IGNORA colaboradores eliminados (E) e aqueles cujo
   * REGISTO foi rejeitado (validação REGISTO_COLABORADOR em I) — senão uma pessoa rejeitada nunca mais
   * podia ser registada. Um colaborador cessado (registo aprovado) continua a contar.
   */
  @org.springframework.data.jpa.repository.Query("""
      select count(f) > 0 from FuncionarioEntity f
       where f.tipoDocumentoId.id = :tipo and f.numDocumento = :num
         and f.estado <> cv.inps.rh.shared.application.constants.Estado.E
         and (:uuid is null or f.uuid <> :uuid)
         and not (f.estado = cv.inps.rh.shared.application.constants.Estado.I
              and exists (select v.id from ValidacaoEntity v
                           where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                             and v.estado = cv.inps.rh.shared.application.constants.Estado.I)
              and not exists (select v.id from ValidacaoEntity v
                               where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                                 and v.estado <> cv.inps.rh.shared.application.constants.Estado.I))""")
  boolean existeDocumentoAtivo(@org.springframework.data.repository.query.Param("tipo") Long tipo,
      @org.springframework.data.repository.query.Param("num") String num,
      @org.springframework.data.repository.query.Param("uuid") UUID uuid);

  /**
   * QA 27/11 (B1): registo de colaborador rejeitado (funcionário I cuja validação REGISTO_COLABORADOR
   * está I e nunca teve outra) com este documento. É reaproveitado por um novo registo da mesma pessoa
   * (a constraint UQ_UNICO_DOC não deixa criar outra linha com o mesmo documento).
   */
  @org.springframework.data.jpa.repository.Query("""
      select f from FuncionarioEntity f
       where f.tipoDocumentoId.id = :tipo and f.numDocumento = :num
         and f.estado = cv.inps.rh.shared.application.constants.Estado.I
         and exists (select v.id from ValidacaoEntity v
                      where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                        and v.estado = cv.inps.rh.shared.application.constants.Estado.I)
         and not exists (select v.id from ValidacaoEntity v
                          where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                            and v.estado <> cv.inps.rh.shared.application.constants.Estado.I)
       order by f.id desc""")
  java.util.List<FuncionarioEntity> findRegistoRejeitadoPorDocumento(
      @org.springframework.data.repository.query.Param("tipo") Long tipo,
      @org.springframework.data.repository.query.Param("num") String num);

  @org.springframework.data.jpa.repository.Query("""
      select count(f) > 0 from FuncionarioEntity f
       where f.nif = :nif
         and f.estado <> cv.inps.rh.shared.application.constants.Estado.E
         and (:uuid is null or f.uuid <> :uuid)
         and not (f.estado = cv.inps.rh.shared.application.constants.Estado.I
              and exists (select v.id from ValidacaoEntity v
                           where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                             and v.estado = cv.inps.rh.shared.application.constants.Estado.I)
              and not exists (select v.id from ValidacaoEntity v
                               where v.funId = f and v.referenciaName = 'REGISTO_COLABORADOR'
                                 and v.estado <> cv.inps.rh.shared.application.constants.Estado.I))""")
  boolean existeNifAtivo(@org.springframework.data.repository.query.Param("nif") Long nif,
      @org.springframework.data.repository.query.Param("uuid") UUID uuid);
}




