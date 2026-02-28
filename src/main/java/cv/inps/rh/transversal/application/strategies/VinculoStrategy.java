package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VinculoStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.VINCULO;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // TiposRelacionamentoEntity (contrVinculoId) -> ContratoEntity (vinculoId) -> ParamVinculoEntity (id/nome)
        return List.of(
                root.get("contrVinculoId").get("vinculoId").get("id").alias("vinculo_id"),
                root.get("contrVinculoId").get("vinculoId").get("nome").alias("vinculo_nome")
        );
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("contrVinculoId").get("vinculoId").get("id"),
                root.get("contrVinculoId").get("vinculoId").get("nome")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
        return root.get("contrVinculoId").get("vinculoId").get("id").in(valores);
    }
}
