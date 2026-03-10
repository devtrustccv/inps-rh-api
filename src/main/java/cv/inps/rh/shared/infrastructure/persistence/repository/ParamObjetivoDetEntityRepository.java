package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamObjetivoDetEntityRepository extends
    JpaRepository<ParamObjetivoDetEntity, Long>,
    JpaSpecificationExecutor<ParamObjetivoDetEntity>
{


  Optional<ParamObjetivoDetEntity> findByUuid(UUID uuid);



}
