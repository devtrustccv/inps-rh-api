package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RhPagamentoEntityRepository extends
    JpaRepository<RhPagamentoEntity, Long>,
    JpaSpecificationExecutor<RhPagamentoEntity> {

  List<RhPagamentoEntity> findByEstadoAndDefp_FunId(String estado, FuncionarioEntity funId);

}
