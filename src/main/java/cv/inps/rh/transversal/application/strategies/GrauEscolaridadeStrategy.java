package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GrauEscolaridadeStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.GRAU_ESCOLARIDADE;
    }

    @SuppressWarnings("unchecked")
    private Join<FuncionarioEntity, HabilitacaoLiterariaEntity> getHabilitacaoJoin(
            Root<TiposRelacionamentoEntity> root) {
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funJoin = null;
        for (Join<TiposRelacionamentoEntity, ?> join : root.getJoins()) {
            if (join.getAttribute().getName().equals("funId")) {
                funJoin = (Join<TiposRelacionamentoEntity, FuncionarioEntity>) join;
                break;
            }
        }
        if (funJoin == null) {
            funJoin = root.join("funId", JoinType.LEFT);
        }

        for (Join<FuncionarioEntity, ?> join : funJoin.getJoins()) {
            if (join.getAttribute().getName().equals("habilitacoesLiterarias")) {
                return (Join<FuncionarioEntity, HabilitacaoLiterariaEntity>) join;
            }
        }
        return funJoin.join("habilitacoesLiterarias", JoinType.LEFT);
    }

    private Expression<String> getExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        Join<FuncionarioEntity, HabilitacaoLiterariaEntity> habJoin = getHabilitacaoJoin(root);
        return habJoin.get("nivel");
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(getExpression(root, cb).alias("grau_escolaridade_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(getExpression(root, cb));
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        return getExpression(root, cb).in(valores);
    }
}
