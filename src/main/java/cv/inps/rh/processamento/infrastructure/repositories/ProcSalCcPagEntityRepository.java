package cv.inps.rh.processamento.infrastructure.repositories;

import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoRowDTO;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcPagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProcSalCcPagEntityRepository extends
    JpaRepository<ProcSalCcPagEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcPagEntity> {

  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO(
              e.tipo,
              e.procSalId,
              sum(e.valor)
          )
          FROM ProcSalCcPagEntity e
          GROUP BY e.tipo, e.procSalId
      """)
  List<ResumoProcessamentoRowDTO> getPagamentos();


  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.DetalhesProcessamentoRowDTO(
              e.nome,
              e.cargo,
              e.relacao,
              e.valor
          )
          FROM ProcSalCcPagEntity e
          WHERE e.tipo = :tipoMovimento AND e.procSalId = :procId
      """)
  List<DetalhesProcessamentoRowDTO> getDetalhesPagamento(
      @Param("tipoMovimento") String tipoMovimento,
      @Param("procId") Long procSalId
  );


}
