package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorAvalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissaoPrestadorAvalEntityRepository extends
    JpaRepository<MissaoPrestadorAvalEntity, Long>,
    JpaSpecificationExecutor<MissaoPrestadorAvalEntity> {

  /** Avaliações de um prestador parametrizado, em todas as missões (ecrã "Ver Avaliação"). */
  List<MissaoPrestadorAvalEntity> findAllByMissaoPrestId_ParamPrestId_IdAndEstadoOrderByIdDesc(Long paramPrestId, String estado);

  Optional<MissaoPrestadorAvalEntity> findFirstByMissaoPrestId_IdAndEstadoOrderByIdDesc(Long missaoPrestId, String estado);
}
