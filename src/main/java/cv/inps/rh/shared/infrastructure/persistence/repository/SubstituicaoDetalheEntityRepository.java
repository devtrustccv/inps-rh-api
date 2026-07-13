package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubstituicaoDetalheEntityRepository extends JpaRepository<SubstituicaoDetalheEntity, Long> {

  List<SubstituicaoDetalheEntity> findBySubstituicaoId_Id(Long substituicaoId);

}
