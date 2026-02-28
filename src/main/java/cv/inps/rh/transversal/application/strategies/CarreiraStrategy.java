package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarreiraStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.CARREIRA;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // CarreiraEntity (carreiraId) -> ParamCarreiraEntity (carrPccsId)
        return List.of(
                root.get("carreiraId").get("carrPccsId").get("id").alias("carreira_id"),
                root.get("carreiraId").get("carrPccsId").get("nome").alias("carreira_nome")
        );
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("carreiraId").get("carrPccsId").get("id"),
                root.get("carreiraId").get("carrPccsId").get("nome")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
        return root.get("carreiraId").get("carrPccsId").get("id").in(valores);
    }
}
