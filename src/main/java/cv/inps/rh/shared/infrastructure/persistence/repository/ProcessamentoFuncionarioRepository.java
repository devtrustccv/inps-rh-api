package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoFuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoFuncionarioRepository extends
    JpaRepository<ProcessamentoFuncionarioEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoFuncionarioEntity> {

  /**
   * Reproduz a coluna PROCESSAMENTO da vista RH_V_CARREIRA: indica se a carreira
   * ja foi processada em folha, i.e. existe algum tiprel dessa carreira com
   * registo em RH_T_PROC_FUNCIONARIOS.
   */
  boolean existsByTiprel_CarreiraId_Id(Long carreiraId);
}
