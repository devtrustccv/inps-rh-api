package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoFuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProcessamentoFuncionarioRepository extends
    JpaRepository<ProcessamentoFuncionarioEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoFuncionarioEntity> {

  // Em batch: das mobilidades dadas, devolve as que já têm processamento salarial
  // (o seu tiprel tem registo em RH_T_PROC_FUNCIONARIOS).
  @Query("SELECT DISTINCT p.tiprel.mobId.id FROM ProcessamentoFuncionarioEntity p WHERE p.tiprel.mobId.id IN :mobIds")
  List<Long> findMobIdsComProcessamento(@Param("mobIds") Collection<Long> mobIds);
}
