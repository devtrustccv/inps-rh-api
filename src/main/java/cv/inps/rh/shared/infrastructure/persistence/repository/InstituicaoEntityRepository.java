package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.projections.InstituicaoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InstituicaoEntityRepository extends
    JpaRepository<InstituicaoEntity, Long>,
    JpaSpecificationExecutor<InstituicaoEntity> {

  default InstituicaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "InstituicaoEntity not found for id: " + id));
  }

  @Query(value = """
    SELECT
        T.ID AS id,
        '(' || T.CODIGO || ') ' || TRIM(REGEXP_REPLACE(T.nome, '^Processar Remunerações', '')) AS nome
    FROM INPSSIGOF.INSTITUICOES T
    WHERE UPPER(T.nome) LIKE 'PROCESSAR REMUNE%'
      AND T.nivel = (
        SELECT MAX(T2.nivel)
        FROM INPSSIGOF.INSTITUICOES T2
        WHERE UPPER(T2.nome) LIKE 'PROCESSAR REMUNE%'
          AND T2.codigo LIKE T.codigo || '%'
          AND NOT EXISTS (
              SELECT 1
              FROM INPSSIGOF.INSTITUICOES T3
              WHERE T3.INSTIT_ID = T2.ID
          )
    )
    """, nativeQuery = true)
  List<InstituicaoProjection> findInstituicoesList();
}
