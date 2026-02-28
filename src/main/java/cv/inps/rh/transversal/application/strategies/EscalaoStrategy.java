package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EscalaoStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.ESCALAO;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // CarreiraEntity (carreiraId) -> ParamEscalaoEntity (escalaoId)
        return List.of(
                root.get("carreiraId").get("escalaoId").get("id").alias("escalao_id"),
                root.get("carreiraId").get("escalaoId").get("escalao").alias("escalao_nome")
        );
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("carreiraId").get("escalaoId").get("id"),
                root.get("carreiraId").get("escalaoId").get("escalao")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
        return root.get("carreiraId").get("escalaoId").get("id").in(valores);
    }
}
