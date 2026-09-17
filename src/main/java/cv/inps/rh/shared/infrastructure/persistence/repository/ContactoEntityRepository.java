package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactoEntityRepository extends
    JpaRepository<ContactoEntity, Long>,
    JpaSpecificationExecutor<ContactoEntity>
{

      default ContactoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ContactoEntity not found for id: " + id));
      }

      boolean existsByContactoAndEstadoNot(String contacto, Estado estado);

      boolean existsByContactoAndFunId_UuidNotAndEstadoNot(String contacto, UUID funUuid, Estado estado);

}
