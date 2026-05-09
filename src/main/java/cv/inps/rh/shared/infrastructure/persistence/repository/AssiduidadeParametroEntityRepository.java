package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeParametroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AssiduidadeParametroEntityRepository extends
    JpaRepository<AssiduidadeParametroEntity, Long>,
    JpaSpecificationExecutor<AssiduidadeParametroEntity> {

  default AssiduidadeParametroEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "AssiduidadeParametroEntity not found for id: " + id));
  }

  List<AssiduidadeParametroEntity> findAllByEstado(String estado);

  @Query("SELECT a.tDispensa FROM AssiduidadeParametroEntity a WHERE a.estado = 'A' AND a.dtFim IS NULL")
  Optional<String> findActiveTDispensa();

}
