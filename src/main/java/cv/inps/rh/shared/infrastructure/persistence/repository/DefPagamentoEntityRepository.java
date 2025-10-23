package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DefPagamentoEntityRepository extends
    JpaRepository<DefPagamentoEntity, Long>,
    JpaSpecificationExecutor<DefPagamentoEntity>
{

      default DefPagamentoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DefPagamentoEntity not found for id: " + id));
      }

}