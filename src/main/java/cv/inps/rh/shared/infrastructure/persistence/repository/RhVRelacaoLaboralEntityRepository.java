package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVRelacaoLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface RhVRelacaoLaboralEntityRepository extends
    JpaRepository<RhVRelacaoLaboralEntity, String>,
    JpaSpecificationExecutor<RhVRelacaoLaboralEntity> {

  /**
   * A relação laboral corrente do colaborador ({@code EST_ACT_ADM = 1}). É daqui que sai o
   * cargo quando é preciso resolver o manual de funções "cujo cargo = cargo do colaborador".
   */
  java.util.Optional<RhVRelacaoLaboralEntity> findFirstByFuncionarioUuidAndEstActAdm(
      String funcionarioUuid, Long estActAdm);

  default RhVRelacaoLaboralEntity findByIdOrThrow(String id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("RhVRelacaoLaboralEntity not found for id: " + id));
  }
}
