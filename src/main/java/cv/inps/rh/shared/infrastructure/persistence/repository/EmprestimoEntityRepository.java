package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.emprestimo.application.dto.EmprestimoListRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmprestimoEntityRepository extends
    JpaRepository<EmprestimoEntity, Long>,
    JpaSpecificationExecutor<EmprestimoEntity> {

  default EmprestimoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EmprestimoEntity not found for id: " + id));
  }

  Optional<EmprestimoEntity> findByUuid(String uuid);

  default EmprestimoEntity findByUuidOrThrow(String uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "EmprestimoEntity not found for uuid: " + uuid
        ));
  }

  List<EmprestimoEntity> findByUuidNotAndTiprel_FunId(String uuid, FuncionarioEntity funcionarioEntity);

  @Query("""
      SELECT new cv.inps.rh.emprestimo.application.dto.EmprestimoListRowDTO(
          p.uuid,
          f.uuid,
          e.tipoSituacao,
          e.estado,
          '',
          f.nome,
          e.tipoEmprestimo,
          "",
          e.renogociacao,
          e.uuid,
          e.valorEmprestimo,
          e.valorAdiantado,
          e.valorReforco,
          e.nrPrestacao,
           (
             SELECT COUNT(pf)
             FROM PlanoFinanceiroEntity pf
             WHERE pf.emprestimo = e
             AND pf.estado = 'A'
             AND pf.flgPago = 'PAGO'
             AND pf.dataPagamento IS NOT NULL
           ),
          e.valorPago,
          e.valorDivida,
          e.dataInicio,
          p.etapa,
          ''
      )
      FROM PedidoEntity p, EmprestimoEntity e, TiposRelacionamentoEntity tr, FuncionarioEntity f, MobilidadeEntity m
      WHERE p.id = e.pedido.id
      AND e.id = (
            SELECT MAX(latestLoan.id)
            FROM EmprestimoEntity latestLoan
            WHERE latestLoan.pedido.id = p.id
      )
      AND e.estado <> 'CANCELADO'
      AND e.tiprel.id = tr.id
      AND tr.estActAdm = 1
      AND tr.funId.id = f.id
      AND tr.mobId.id = m.id
      AND (:tipoEmprestimo IS NULL OR e.tipoEmprestimo = :tipoEmprestimo)
      AND (:estado IS NULL OR e.estado = :estado)
      AND (
            (:dataInicio IS NULL OR :dataFim IS NULL) OR (e.dataInicio BETWEEN :dataInicio AND :dataFim)
      )
      AND (:direccaoId IS NULL OR m.instidId.id = :direccaoId)
      AND (:funcionarioId IS NULL OR f.uuid = :funcionarioId)
      """)
  Page<EmprestimoListRowDTO> listLoans(
      String tipoEmprestimo,
      String estado,
      LocalDate dataInicio,
      LocalDate dataFim,
      Long direccaoId,
      UUID funcionarioId,
      Pageable pageable
  );

}
