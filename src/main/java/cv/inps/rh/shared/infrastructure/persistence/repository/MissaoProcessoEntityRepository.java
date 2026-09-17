package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoProcessoEntityRepository extends
    JpaRepository<MissaoProcessoEntity, Long>,
    JpaSpecificationExecutor<MissaoProcessoEntity> {

  List<MissaoProcessoEntity> findAllByMissaoServId_UuidOrderByIdAsc(UUID missaoUuid);

  List<MissaoProcessoEntity> findAllByMissaoServId_IdIn(Collection<Long> missaoIds);

  Optional<MissaoProcessoEntity> findByMissaoServId_UuidAndTipoProcesso(UUID missaoUuid, String tipoProcesso);

  Optional<MissaoProcessoEntity> findByUuid(UUID uuid);

  default MissaoProcessoEntity findByMissaoAndTipoOrThrow(UUID missaoUuid, String tipoProcesso) {
    return findByMissaoServId_UuidAndTipoProcesso(missaoUuid, tipoProcesso)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "Processo " + tipoProcesso + " não encontrado na missão: " + missaoUuid));
  }
}
