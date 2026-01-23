package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
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


  /*@Query(value = """
      SELECT *
      FROM glb_t_geografia g
      WHERE g.nivel_detalhe = :nivelDetalhe
      AND (:geogrId IS NULL OR g.geogr_id = :geogrId)
      ORDER BY g.id
      """, nativeQuery = true)
  List<GeografiaEntity> findByNivelDetalheAndGeogrId(
      @Param("nivelDetalhe") Long nivelDetalhe,
      @Param("geogrId") Long geogrId
  );*/

  @Query("SELECT g FROM GeografiaEntity g " +
      "WHERE g.nivelDetalhe = :nivelDetalhe " +
      "AND (:geogrId IS NULL OR g.geogrId = :geogrId) " +
      "ORDER BY g.id")
  List<GeografiaEntity> findByNivelDetalheAndGeogrId(
      @Param("nivelDetalhe") Long nivelDetalhe,
      @Param("geogrId") Long geogrId
  );



}
