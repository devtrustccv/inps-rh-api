package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlanoFinanceiroEntityRepository extends
    JpaRepository<PlanoFinanceiroEntity, Long>,
    JpaSpecificationExecutor<PlanoFinanceiroEntity> {

  List<PlanoFinanceiroEntity> findAllByEmprestimo(EmprestimoEntity emprestimo);

  // Regenerar o plano no Contrato (Gravar e Seguinte chamam-no): apagar o anterior primeiro —
  // ainda não há pagamentos nesta fase; inativá-lo deixaria linhas 'I' que reativarPlanos
  // voltaria a ligar ao cancelar um subprocesso.
  @Transactional
  @Modifying
  @Query("DELETE FROM PlanoFinanceiroEntity p WHERE p.emprestimo.id = :emprestimoId")
  void apagarPlanos(@Param("emprestimoId") Long emprestimoId);

  @Transactional
  @Modifying
  @Query("""
          UPDATE PlanoFinanceiroEntity p
             SET p.estado = 'I'
           WHERE p.emprestimo.id = :emprestimoId
      """)
  void inativarPlanos(@Param("emprestimoId") Long emprestimoId);

  // Fundo Social/Recuperação geram o plano em estado 'P' (pendente) na
  // submissão do pedido; a Validação Empréstimo (SIM) ativa-o.
  @Transactional
  @Modifying
  @Query("""
          UPDATE PlanoFinanceiroEntity p
             SET p.estado = 'A'
           WHERE p.emprestimo.id = :emprestimoId
             AND p.estado = 'P'
      """)
  void ativarPlanosPendentes(@Param("emprestimoId") Long emprestimoId);

  // Cancelar um subprocesso (Amortização / Renegociação) depois do NEXT do pedido: o
  // EmprestimoHelper.saveByTipoSituacao tinha inativado o plano do empréstimo anterior.
  @Transactional
  @Modifying
  @Query("""
          UPDATE PlanoFinanceiroEntity p
             SET p.estado = 'A'
           WHERE p.emprestimo.id = :emprestimoId
             AND p.estado = 'I'
      """)
  void reativarPlanos(@Param("emprestimoId") Long emprestimoId);

  long countByEmprestimo_IdAndEstado(Long emprestimoId, String estado);
}
