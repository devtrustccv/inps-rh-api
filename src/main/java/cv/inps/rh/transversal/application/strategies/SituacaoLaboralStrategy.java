package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SituacaoLaboralStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.SITUACAO_LABORAL;
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // TiposRelacionamentoEntity (situacLaboralId) -> SituacaoLaboralEntity
        // (situacaoLaboralId) -> ParamSituacaoEntity (id/nome)
        return List.of(
                root.get("situacLaboralId").get("situacaoLaboralId").get("id").alias("situacao_laboral_id"),
                root.get("situacLaboralId").get("situacaoLaboralId").get("nome").alias("situacao_laboral_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(
                root.get("situacLaboralId").get("situacaoLaboralId").get("id"),
                root.get("situacLaboralId").get("situacaoLaboralId").get("nome"));
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        return root.get("situacLaboralId").get("situacaoLaboralId").get("id").in(valores);
    }
}
