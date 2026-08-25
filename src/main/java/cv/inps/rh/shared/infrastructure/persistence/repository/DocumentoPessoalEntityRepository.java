package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;


@Repository
@JaversSpringDataAuditable
public interface DocumentoPessoalEntityRepository extends
    JpaRepository<DocumentoPessoalEntity, Long>,
    JpaSpecificationExecutor<DocumentoPessoalEntity>
{

      default DocumentoPessoalEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DocumentoPessoalEntity not found for id: " + id));
      }

}
