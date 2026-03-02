package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MobilidadeStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.MOBILIDADE;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // TiposRelacionamentoEntity (mobId) -> MobilidadeEntity (tipoSituacao)
        // Usando o campo tipoSituacao como ID e Descrição, pois não parece haver uma tabela de domínio externa
        return List.of(
                root.get("mobId").get("tipoSituacao").alias("mobilidade_id"),
                root.get("mobId").get("tipoSituacao").alias("mobilidade_nome")
        );
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("mobId").get("tipoSituacao")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
        return root.get("mobId").get("tipoSituacao").in(valores);
    }
}
