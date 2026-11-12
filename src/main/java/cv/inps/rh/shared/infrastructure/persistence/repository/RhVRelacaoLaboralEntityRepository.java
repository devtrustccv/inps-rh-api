package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RhVRelacaoLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface RhVRelacaoLaboralEntityRepository extends
    JpaRepository<RhVRelacaoLaboralEntity, Long>,
    JpaSpecificationExecutor<RhVRelacaoLaboralEntity> {

  /**
   * A relação laboral corrente do colaborador ({@code EST_ACT_ADM = 1}). É daqui que sai o
   * cargo quando é preciso resolver o manual de funções "cujo cargo = cargo do colaborador".
   */
  java.util.Optional<RhVRelacaoLaboralEntity> findFirstByFuncionarioUuidAndEstActAdm(
      String funcionarioUuid, Long estActAdm);

  /** Qualquer linha do colaborador, preferindo a actual (EST_ACT_ADM = 1). */
  java.util.Optional<RhVRelacaoLaboralEntity> findFirstByFuncionarioUuidOrderByEstActAdmDesc(String funcionarioUuid);
}
