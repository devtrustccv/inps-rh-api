package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoAtitudePessoalEntityRepository extends
        JpaRepository<AvaliacaoAtitudePessoalEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoAtitudePessoalEntity> {

    Optional<AvaliacaoAtitudePessoalEntity> findByUuid(UUID uuid);

    List<AvaliacaoAtitudePessoalEntity> findAllByAvaliacao_Uuid(UUID uuid);
}
