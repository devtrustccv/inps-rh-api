package cv.inps.rh.processamento.infrastructure.repositories;

import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoRowDTO;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcRemunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProcSalCcRemunEntityRepository extends
    JpaRepository<ProcSalCcRemunEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcRemunEntity> {

  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO(
              e.itemActo,
              e.descricao,
              e.tipo,
              e.shortDesc,
              e.procSalId,
              COUNT(DISTINCT e.tiprelId),
              SUM(e.valor),
              null,
              null
          )
          FROM ProcSalCcRemunEntity e
          WHERE e.procSalId IN (:procId)
          GROUP BY e.itemActo,
                e.descricao,
                   e.tipo,
                   e.shortDesc,
                   e.procSalId
      """)
  List<ResumoProcessamentoRowDTO> getRemuneracoesForOne(@Param("procId") Long procId);

  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO(
              e.itemActo,
              e.descricao,
              e.tipo,
              e.shortDesc,
              null,
              COUNT(DISTINCT e.tiprelId),
              SUM(e.valor),
              null,
              null
          )
          FROM ProcSalCcRemunEntity e
          WHERE e.procSalId IN (:procId)
          GROUP BY e.itemActo,
                e.descricao,
                   e.tipo,
                   e.shortDesc
      """)
  List<ResumoProcessamentoRowDTO> getRemuneracoes(@Param("procId") List<Long> procId);


  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.DetalhesProcessamentoRowDTO(
              e.nome,
              e.cargo,
              e.relacao,
              e.valor,
              e.descricao,
              null
          )
          FROM ProcSalCcRemunEntity e
          WHERE e.tipo = :tipoMovimento
                AND e.procSalId = :procId
                AND (:idItemAto IS NULL OR e.itemActo = :idItemAto)
      """)
  List<DetalhesProcessamentoRowDTO> getDetalhesRemuneracao(
      @Param("tipoMovimento") String tipoMovimento,
      @Param("procId") Long procSalId,
      @Param("idItemAto") Long idItemAto
  );

  List<ProcSalCcRemunEntity> findByProcSalId(Long procFuncId);

  List<ProcSalCcRemunEntity> findByProcSalIdAndFunId(Long procFuncId, Long funId);

}
