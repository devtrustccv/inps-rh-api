package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
      Pageable pageable
  );
}
