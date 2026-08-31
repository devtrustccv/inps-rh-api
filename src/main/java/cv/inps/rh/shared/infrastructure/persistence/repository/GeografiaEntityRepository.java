package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GeografiaEntityRepository extends
    JpaRepository<GeografiaEntity, Long>,
    JpaSpecificationExecutor<GeografiaEntity> {

  default GeografiaEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "GeografiaEntity not found for id: " + id));
  }

  @Query("""
      SELECT g.nome
      FROM GeografiaEntity g
      WHERE g.id = :id
      """)
  String getDescriptionById(Long id);

  @Query("SELECT g FROM GeografiaEntity g " +
         "WHERE g.nivelDetalhe = :nivelDetalhe " +
         "AND (:geogrId IS NULL OR g.geogrId = :geogrId) " +
         "ORDER BY g.id")
  List<GeografiaEntity> findByNivelDetalheAndGeogrId(
      @Param("nivelDetalhe") Long nivelDetalhe,
      @Param("geogrId") Long geogrId
  );

  @Query("""
      SELECT e
      FROM GeografiaEntity e
      WHERE e.nivelDetalhe = 1 AND (:paisId IS NULL OR e.id = :paisId) AND (:name IS NULL OR LOWER(e.nomeNorm) LIKE :name)
      """)
  Page<GeografiaEntity> findCountries(
      @Param("paisId") Long paisId,
      @Param("name") String name,
      Pageable pageable
  );
}
