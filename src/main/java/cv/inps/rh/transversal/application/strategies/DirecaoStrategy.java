package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DirecaoStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.DIRECAO;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("mobId").get("instidId").get("id").alias("direcao_id"),
                root.get("mobId").get("instidId").get("nome").alias("direcao_nome")
        );
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("mobId").get("instidId").get("id"),
                root.get("mobId").get("instidId").get("nome")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
        return root.get("mobId").get("instidId").get("id").in(valores);
    }
}
