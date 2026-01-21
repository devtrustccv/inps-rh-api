package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.http.HttpStatus;
import java.util.Optional;



@Repository
public interface AssiduidadeSinteseDiarioEntityRepository extends
    JpaRepository<AssiduidadeSinteseDiarioEntity, Long>,
    JpaSpecificationExecutor<AssiduidadeSinteseDiarioEntity>
{

      default AssiduidadeSinteseDiarioEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"AssiduidadeSinteseDiarioEntity not found for id: " + id));
      }

  @EntityGraph(attributePaths = {"funcionarioId"})
  org.springframework.data.domain.Page<AssiduidadeSinteseDiarioEntity> findAll(
      org.springframework.data.jpa.domain.Specification<AssiduidadeSinteseDiarioEntity> spec,
      org.springframework.data.domain.Pageable pageable
  );

}
