package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.DadosApoliceResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosApoliceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosApoliceEntityRepository extends JpaRepository<DadosApoliceEntity, Long>, JpaSpecificationExecutor<DadosApoliceEntity> {

  @Query("""
      SELECT NEW cv.inps.rh.processamento.application.dto.DadosApoliceResponseDTO(
        d.id,
        d.dadosInstituicao.uuid,
        d.numApolice,
        d.ilhaId,
        d.dataApolice,
        d.estado
      )
      FROM DadosApoliceEntity d
      WHERE d.estado = :estado
      """
  )
  Page<DadosApoliceResponseDTO> findAllByEstado(
      @Param("estado") String estado,
      Pageable page
  );
}
