package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DadosApoliceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DadosApoliceEntityRepository extends
    JpaRepository<DadosApoliceEntity, Long>,
    JpaSpecificationExecutor<DadosApoliceEntity> {

  Optional<DadosApoliceEntity> findFirstByIlhaIdAndEstadoOrderByIdDesc(
      Long ilhaId,
      String estado
  );

  List<DadosApoliceEntity> findAllByEstadoOrderByIlhaIdAsc(String estado);

  Optional<DadosApoliceEntity> findFirstByUuidAndEstadoOrderByIdDesc(
      String uuid,
      String estado
  );
}
