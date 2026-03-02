package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EstruturaRemuneratoriaStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.ESTRUTURA_REMUNERATORIA;
    }

    private Expression<String> getExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Usar joins implícitos para garantir que Hibernate reutilize os joins
        Path<CarreiraEntity> carreiraPath = root.get("carreiraId");
        Path<ParamCarreiraEntity> pccsPath = carreiraPath.get("carrPccsId");
        Path<ParamEscalaoEntity> escalaoPath = carreiraPath.get("escalaoId");

        Expression<String> carreiraNome = pccsPath.get("nome");
        Expression<String> escalaoNome = escalaoPath.get("nome");
        Expression<String> salarioStr = cb.function("TO_CHAR", String.class, carreiraPath.get("salario"));

        // Concatenar: Carreira - Escalão - Salário
        Expression<String> part1 = cb.concat(carreiraNome, " - ");
        Expression<String> part2 = cb.concat(part1, escalaoNome);
        Expression<String> part3 = cb.concat(part2, " - ");
        
        return cb.concat(part3, salarioStr);
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        return List.of(getExpression(root, cb).alias("estrutura_remuneratoria_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Agrupar pelos componentes individuais para evitar ORA-00979
        Path<CarreiraEntity> carreiraPath = root.get("carreiraId");
        
        return List.of(
            carreiraPath.get("carrPccsId").get("nome"),
            carreiraPath.get("escalaoId").get("nome"),
            carreiraPath.get("salario")
        );
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        // Filtrar pela string concatenada
        return getExpression(root, cb).in(valores);
    }
}
