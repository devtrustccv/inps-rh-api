package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PedidoDecisaoEntityRepository extends
    JpaRepository<PedidoDecisaoEntity, Long>,
    JpaSpecificationExecutor<PedidoDecisaoEntity> {

  default PedidoDecisaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "PedidoDecisaoEntity not found for id: " + id));
  }

  Optional<PedidoDecisaoEntity> findByUuid(String uuid);

  default PedidoDecisaoEntity findByUuidOrThrow(String uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "PedidoDecisaoEntity not found for uuid: " + uuid
        ));
  }

  Optional<PedidoDecisaoEntity> findByPedidoAndEtapaAndEstado(PedidoEntity pedido, String etapa, String estado);

  List<PedidoDecisaoEntity> findByPedidoAndEtapaInAndEstado(PedidoEntity pedido, List<String> etapa, String estado);

}
