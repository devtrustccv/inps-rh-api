package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessamentoSalarialEntityRepository extends
    JpaRepository<ProcessamentoSalarialEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoSalarialEntity> {

  default ProcessamentoSalarialEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("ProcessamentoSalarialEntity not found for id: " + id));
  }

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
          UPDATE ProcessamentoSalarialEntity p
             SET p.estado = :novoEstado,
                 p.userValidProv = COALESCE(:userValidProv, p.userValidProv),
                 p.userValidDef = COALESCE(:userValidDef, p.userValidDef),
                 p.userCabimento = COALESCE(:userCabimento, p.userCabimento),
                 p.userAutorizacao = COALESCE(:userAutorizacao, p.userAutorizacao)
           WHERE p.id IN :ids
             AND p.estado IN :estadosPermitidos
          """)
  int atualizarEstadoEUtilizadores(
          @Param("ids") List<Long> ids,
          @Param("estadosPermitidos") List<String> estadosPermitidos,
          @Param("novoEstado") String novoEstado,
          @Param("userValidProv") String userValidProv,
          @Param("userValidDef") String userValidDef,
          @Param("userCabimento") String userCabimento,
          @Param("userAutorizacao") String userAutorizacao
  );
}

