package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoOutroEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoOutroEntityRepository extends
    JpaRepository<EmprestimoOutroEntity, Long>,
    JpaSpecificationExecutor<EmprestimoOutroEntity> {

  default EmprestimoOutroEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EmprestimoOutroEntity not found for id: " + id));
  }

  Optional<EmprestimoOutroEntity> findByUuid(String uuid);

  default EmprestimoOutroEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("EmprestimoOutroEntity not found for uuid: " + uuid));
  }

  List<EmprestimoOutroEntity> findByReferenciaOrigemAndFunAndEstado(PedidoEntity referenciaOrigem, FuncionarioEntity fun, String estado);
}
