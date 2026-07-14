package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.projections.DirecaoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirecaoEntityRepository extends
    JpaRepository<DirecaoEntity, Long>,
    JpaSpecificationExecutor<DirecaoEntity> {

  default DirecaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DirecaoEntity not found for id: " + id));
  }

  @Query(value = """
    SELECT T.ID AS id, T.NOME AS nome
    FROM RH_T_DIRECAO T
    WHERE T.ESTADO = 'A'
    ORDER BY T.NOME
    """, nativeQuery = true)
  List<DirecaoProjection> findDirecoesList();

  @Query(value = "SELECT GET_NOME_CENTRO_CUSTO(:institId) FROM dual", nativeQuery = true)
  String getNomeCentroCusto(@Param("institId") Long institId);
}
