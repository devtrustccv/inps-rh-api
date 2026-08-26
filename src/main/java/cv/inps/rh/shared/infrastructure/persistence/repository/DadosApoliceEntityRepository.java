package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DadosApoliceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DadosApoliceEntityRepository extends JpaRepository<DadosApoliceEntity, Long>, JpaSpecificationExecutor<DadosApoliceEntity> {

  Optional<DadosApoliceEntity> findFirstByIlhaIdAndEstadoOrderByIdDesc(
      Long ilhaId,
      String estado
  );

  Page<DadosApoliceEntity> findAllByEstado(String estado, Pageable page);
}
