package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CargoStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.CARGO;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("cargoId").get("id").alias("cargo_id"),
                root.get("cargoId").get("nome").alias("cargo_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("cargoId").get("id"),
                root.get("cargoId").get("nome"));
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        return root.get("cargoId").get("id").in(valores);
    }
}
