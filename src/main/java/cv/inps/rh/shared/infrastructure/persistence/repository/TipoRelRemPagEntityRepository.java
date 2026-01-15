package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@Repository
public interface TipoRelRemPagEntityRepository extends
        JpaRepository<TipoRelRemPagEntity, Long>,
        JpaSpecificationExecutor<TipoRelRemPagEntity> {

    default TipoRelRemPagEntity findByIdOrThrow(Long id) {
        return this.findById(id)
                .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                        "TipoRelRemPagEntity not found for id: " + id));
    }

    boolean existsByTipRelIdAndRemId(TiposRelacionamentoEntity tipRelId, DefinicaoRemuneracaoEntity remId);

    boolean existsByTipRelIdAndPagId(TiposRelacionamentoEntity tipRelId, DefPagamentoEntity pagId);

}
