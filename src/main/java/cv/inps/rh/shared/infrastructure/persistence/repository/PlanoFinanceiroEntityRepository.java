package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlanoFinanceiroEntityRepository extends
    JpaRepository<PlanoFinanceiroEntity, Long>,
    JpaSpecificationExecutor<PlanoFinanceiroEntity> {

  List<PlanoFinanceiroEntity> findAllByEmprestimo(EmprestimoEntity emprestimo);
  List<PlanoFinanceiroEntity> findAllByEmprestimoAndEstadoNot(EmprestimoEntity emprestimo, String estado);
}
