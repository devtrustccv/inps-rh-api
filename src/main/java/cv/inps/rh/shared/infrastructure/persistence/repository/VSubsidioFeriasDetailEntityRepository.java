package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.SubsidioFeriasDetalheDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.VSubsidioFeriasDetailEntity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface VSubsidioFeriasDetailEntityRepository extends
    JpaRepository<VSubsidioFeriasDetailEntity, Long>,
    JpaSpecificationExecutor<VSubsidioFeriasDetailEntity> {

  @Query("""
      SELECT NEW cv.inps.rh.processamento.application.dto.SubsidioFeriasDetalheDTO(
           sf.funNome,
           sf.funId,
           sf.dataInicio,
           sf.dataFim,
           sf.escalacaoDesc,
           sf.valorEscalao,
           sf.mesTrab,
           sf.valorMes,
           sf.diasTrab,
           sf.valorDia,
           COALESCE(sf.valorDia, 0) + COALESCE(sf.valorMes, 0),
           sf.situacao,
           null,
           null,
           null
      )
      FROM VSubsidioFeriasDetailEntity sf
      WHERE sf.funId = :funId
      AND sf.anoReferente = :ano
      """)
  List<SubsidioFeriasDetalheDTO> getDetails(
      @Param("funId") Long funId,
      @Param("ano") Integer ano
  );

}
