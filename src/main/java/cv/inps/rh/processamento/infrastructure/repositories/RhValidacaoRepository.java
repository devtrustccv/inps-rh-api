package cv.inps.rh.processamento.infrastructure.repositories;

import cv.inps.rh.processamento.infrastructure.persistence.entity.RhValidacaoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RhValidacaoRepository
    extends JpaRepository<RhValidacaoView, Long>, JpaSpecificationExecutor<RhValidacaoView> {
}
