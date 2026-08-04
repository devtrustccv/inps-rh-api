package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssiduidadeSinteseDiarioEntityRepository extends
    JpaRepository<AssiduidadeSinteseDiarioEntity, Long>,
    JpaSpecificationExecutor<AssiduidadeSinteseDiarioEntity> {

  default AssiduidadeSinteseDiarioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "AssiduidadeSinteseDiarioEntity not found for id: " + id));
  }

  List<AssiduidadeSinteseDiarioEntity> findAllByFuncionarioIdAndDataBetween(FuncionarioEntity funcionarioId, LocalDate dataAfter, LocalDate dataBefore);

  /**
   * Síntese de um colaborador num dia.
   *
   * <p>Devolve lista porque a tabela não tem constraint única em (FUNCIONARIO_ID, DATA)
   * e existem dias com mais do que uma — marcar uma falta criava sempre um registo novo,
   * mesmo que o relógio já tivesse importado esse dia.
   */
  List<AssiduidadeSinteseDiarioEntity> findAllByFuncionarioIdAndData(
      FuncionarioEntity funcionarioId, LocalDate data);
}
