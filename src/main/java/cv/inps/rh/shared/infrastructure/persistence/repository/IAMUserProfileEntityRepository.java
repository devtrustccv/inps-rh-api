package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAMUserProfileEntityRepository extends
    JpaRepository<IAMUserProfileEntity, UUID>,
    JpaSpecificationExecutor<IAMUserProfileEntity> {

  default IAMUserProfileEntity findByIdOrThrow(UUID id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("IAMUserProfileEntity not found for id: " + id));
  }

  Optional<IAMUserProfileEntity> findByUsername(String username);

  Optional<IAMUserProfileEntity> findByEmailIgnoreCase(String email);

  Optional<IAMUserProfileEntity> findBySub(String sub);

  // Id numérico do sistema legacy (bridge com os dados de negócio antigos).
  Optional<IAMUserProfileEntity> findByUserIdOld(String userIdOld);

  List<IAMUserProfileEntity> findBySubIn(Collection<String> subs);

  List<IAMUserProfileEntity> findByUserIdOldIn(Collection<String> userIdsOld);

  @Query("select p from IAMUserProfileEntity p where lower(p.email) in :emails")
  List<IAMUserProfileEntity> findByEmailInIgnoreCase(@Param("emails") Collection<String> emails);
}
