package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public interface DomainEntityRepository extends
    JpaRepository<DomainEntity, Long>,
    JpaSpecificationExecutor<DomainEntity> {

  default DomainEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DomainEntity not found for id: " + id));
  }

  List<DomainEntity> findByDominioAndEstado(String dominio, Estado estado);

  default Map<String, String> getActiveDomainByCode(String dominio) {
    return findByDominioAndEstado(dominio, Estado.A).stream()
        .collect(Collectors.toMap(DomainEntity::getValor, DomainEntity::getDescricao));
  }


}
