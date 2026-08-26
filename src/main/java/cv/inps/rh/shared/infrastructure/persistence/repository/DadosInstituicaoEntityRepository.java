package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosInstituicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DadosInstituicaoEntityRepository extends
    JpaRepository<DadosInstituicaoEntity, Long>,
    JpaSpecificationExecutor<DadosInstituicaoEntity> {

  default DadosInstituicaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DadosInstituicaoEntity not found for id: " + id));
  }

  Optional<DadosInstituicaoEntity> findByUuid(String uuid);

  Optional<DadosInstituicaoEntity> findFirstByEstadoOrderByIdDesc(String estado);

  default DadosInstituicaoEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("DadosInstituicaoEntity not found for uuid: " + uuid));
  }

}
