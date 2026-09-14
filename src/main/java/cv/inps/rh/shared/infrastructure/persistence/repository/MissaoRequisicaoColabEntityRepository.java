package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoColabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MissaoRequisicaoColabEntityRepository extends
    JpaRepository<MissaoRequisicaoColabEntity, Long>,
    JpaSpecificationExecutor<MissaoRequisicaoColabEntity> {

  List<MissaoRequisicaoColabEntity> findAllByMissaoRequisicaoId_IdIn(Collection<Long> missaoRequisicaoIds);
}
