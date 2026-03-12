package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoEntityRepository extends
        JpaRepository<AvaliacaoEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoEntity> {

    @Query("""
                SELECT a
                FROM AvaliacaoEntity a
                WHERE a.funcionario.id = :funId
                ORDER BY a.ano DESC
            """)
    List<AvaliacaoEntity> findUltimasAvaliacoes(
            @Param("funId") Long funId,
            Pageable pageable);

    boolean existsByFuncionario_IdAndAnoAndSemestre(Long funId, Integer ano, String semestre);

    boolean existsByFuncionario_UuidAndAnoAndSemestre(UUID uuid, Integer ano, String semestre);

    Optional<AvaliacaoEntity> findByUuid(UUID uuid);

    default AvaliacaoEntity findByUuidOrThrow(UUID uuid) {
        return findByUuid(uuid)
                .orElseThrow(() -> cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException
                        .notFound("AvaliacaoEntity not found for id: " + uuid));
    }
}
