package cv.inps.rh.processamento.infrastructure.repositories;

import cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcRemunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProcSalCcRemunEntityRepository extends
    JpaRepository<ProcSalCcRemunEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcRemunEntity> {

  @Query("""
          SELECT new cv.inps.rh.processamento.application.dto.ResumoProcessamentoRowDTO(
              e.tipo,
              sum(e.valor)
          )
          FROM ProcSalCcRemunEntity e
          GROUP BY e.tipo
      """)
  List<ResumoProcessamentoRowDTO> getRemuneracoes();

}
