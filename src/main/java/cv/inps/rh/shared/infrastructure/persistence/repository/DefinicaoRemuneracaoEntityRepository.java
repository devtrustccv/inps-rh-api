package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DefinicaoRemuneracaoEntityRepository extends
    JpaRepository<DefinicaoRemuneracaoEntity, Long>,
    JpaSpecificationExecutor<DefinicaoRemuneracaoEntity>
{

      default DefinicaoRemuneracaoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DefinicaoRemuneracaoEntity not found for id: " + id));
      }

}