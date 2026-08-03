package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.assiduidade.application.dto.RegularizacaoContaRequestDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegularizacaoSdoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegularizacaoSdoEntityRepository extends JpaRepository<RegularizacaoSdoEntity, Long>, JpaSpecificationExecutor<RegularizacaoSdoEntity> {

  default RegularizacaoSdoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "RegularizacaoSdoEntity not found for id: " + id));
  }

  @Query("""
      SELECT new cv.inps.rh.assiduidade.application.dto.RegularizacaoContaRequestDTO(
          r.mesReferente,
          r.sdoRecebido,
          r.valorRetroativoSalario,
          r.valorRetroativoSdo,
          r.uuid,
          pf.totLiquido,
          pf.totRemunCollect,
          pf.id,
          r.abonoBeneficio.id,
          r.estado
      )
      FROM ProcessamentoFuncionarioEntity pf
      LEFT JOIN RegularizacaoSdoEntity r
          ON r.procFun.id = pf.id
          AND r.estado = :regularizacaoEstado
      WHERE pf.tiprel.funId.uuid = :funId AND pf.dataProcessamento BETWEEN :start AND :end
      """)
  List<RegularizacaoContaRequestDTO> findRegularizacoesByFunId(
      @Param("funId") UUID funUuid,
      @Param("regularizacaoEstado") String regularizacaoEstado,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end
  );

  Optional<RegularizacaoSdoEntity> findByUuid(String uuid);
}
