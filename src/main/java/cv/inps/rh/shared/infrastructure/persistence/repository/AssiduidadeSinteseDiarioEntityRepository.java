package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.assiduidade.infrastructure.persistence.projections.AssiduidadeResumoViewRow;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

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
}
