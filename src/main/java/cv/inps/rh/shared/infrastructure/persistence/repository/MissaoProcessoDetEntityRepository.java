package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoDetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissaoProcessoDetEntityRepository extends
    JpaRepository<MissaoProcessoDetEntity, Long>,
    JpaSpecificationExecutor<MissaoProcessoDetEntity> {

  List<MissaoProcessoDetEntity> findAllByMissaoProcessoId_IdOrderByIdAsc(Long missaoProcessoId);

  /** Pareceres do ciclo actual de um responsável (P = rascunho, A = emitido; I = anulado por devolução). */
  List<MissaoProcessoDetEntity> findAllByMissaoProcessoId_IdAndResponsavelAndEstadoInOrderByIdDesc(
      Long missaoProcessoId, String responsavel, java.util.Collection<String> estados);
}
