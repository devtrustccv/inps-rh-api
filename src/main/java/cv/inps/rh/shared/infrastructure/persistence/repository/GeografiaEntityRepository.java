package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;



@Repository
public interface GeografiaEntityRepository extends
    JpaRepository<GeografiaEntity, Long>,
    JpaSpecificationExecutor<GeografiaEntity>
{

      default GeografiaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"GeografiaEntity not found for id: " + id));
      }

  @Query(value = """
            SELECT * FROM glb_t_geografia g
            WHERE g.nivel_detalhe = :nivelDetalhe
            AND (:geogrId IS NULL OR g.geogr_id = :geogrId)
            ORDER BY g.id
            """, nativeQuery = true)
  List<GeografiaEntity> findByNivelDetalheAndGeogrId(
      @Param("nivelDetalhe") Long nivelDetalhe,
      @Param("geogrId") Long geogrId
  );


}
