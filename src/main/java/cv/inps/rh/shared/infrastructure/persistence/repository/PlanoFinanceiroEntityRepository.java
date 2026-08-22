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

  @Transactional
  @Modifying
  @Query("""
          UPDATE PlanoFinanceiroEntity p
             SET p.estado = 'I'
           WHERE p.emprestimo.id = :emprestimoId AND p.flgPago <> 'PAGO'
      """)
  int inativarPlanosNaoPagos(@Param("emprestimoId") Long emprestimoId);
}
