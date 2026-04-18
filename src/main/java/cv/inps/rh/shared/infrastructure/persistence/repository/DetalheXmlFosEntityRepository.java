package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.DetalhesFosXmlRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DetalheXmlFosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DetalheXmlFosEntityRepository extends
    JpaRepository<DetalheXmlFosEntity, Long>,
    JpaSpecificationExecutor<DetalheXmlFosEntity> {

  default DetalheXmlFosEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("DetalheXmlFosEntity not found for id: " + id));
  }

  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.DetalhesFosXmlRowDTO(
              d.id,
              f.id,
              d.nuSegurado,
              f.nome,
              d.dirServId,
              d.nuTrabAuto,
              d.vlRemunAuto,
              d.tipo
          )
          FROM DetalheXmlFosEntity d
          JOIN d.idFunc f
          WHERE d.idXmlFos.id = :fosId AND (:direcaoId IS NULL OR d.dirServId = :direcaoId)
      """)
  List<DetalhesFosXmlRowDTO> findDetalhesByFos(@Param("fosId") Long fosId, @Param("direcaoId") Integer direcaoId);
}
